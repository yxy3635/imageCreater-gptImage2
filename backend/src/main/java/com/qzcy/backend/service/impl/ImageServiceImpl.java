package com.qzcy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzcy.backend.entity.ImageGenerationConfig;
import com.qzcy.backend.entity.ImageRecord;
import com.qzcy.backend.exception.BusinessException;
import com.qzcy.backend.mapper.ImageRecordMapper;
import com.qzcy.backend.service.ImageGenerationConfigService;
import com.qzcy.backend.service.ImageService;
import com.qzcy.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRecordMapper imageRecordMapper;
    private final PaymentService paymentService;
    private final ImageGenerationConfigService configService;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${app.upload.image-path:userImage/}")
    private String imagePath;

    private static final Duration IMAGE_TIMEOUT = Duration.ofMinutes(10);

    @Override
    public ImageRecord generate(Long userId, String username, String prompt, String qualityCode) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new BusinessException(400, "提示词不能为空");
        }
        ImageGenerationConfig config = configService.requireEnabled(qualityCode);
        ImageRecord record = new ImageRecord();
        record.setUserId(userId);
        record.setPrompt(prompt.trim());
        record.setStatus("pending");
        record.setCost(config.getPrice());
        record.setCreatedAt(LocalDateTime.now());
        imageRecordMapper.insert(record);

        try {
            paymentService.deductBalance(userId, config.getPrice());
            ImageResponse response = imageModel(config).call(new ImagePrompt(prompt.trim(), options(config)));
            String remoteUrl = response.getResult().getOutput().getUrl();
            String relativePath = saveRemoteImage(username, remoteUrl);
            record.setGeneratedImageUrl(relativePath);
            record.setStatus("success");
            imageRecordMapper.updateById(record);
            return record;
        } catch (BusinessException ex) {
            record.setStatus("failed");
            imageRecordMapper.updateById(record);
            throw ex;
        } catch (Exception ex) {
            record.setStatus("failed");
            imageRecordMapper.updateById(record);
            throw new BusinessException(500, "图像生成失败：" + ex.getMessage());
        }
    }

    @Override
    public Page<ImageRecord> history(Long userId, long page, long size) {
        return imageRecordMapper.selectPage(
                Page.of(page, size),
                new LambdaQueryWrapper<ImageRecord>()
                        .eq(ImageRecord::getUserId, userId)
                        .orderByDesc(ImageRecord::getCreatedAt)
        );
    }

    private String saveRemoteImage(String username, String remoteUrl) throws Exception {
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(IMAGE_TIMEOUT)
                .setReadTimeout(IMAGE_TIMEOUT)
                .build();
        byte[] bytes = restTemplate.getForObject(remoteUrl, byte[].class);
        if (bytes == null || bytes.length == 0) {
            throw new IllegalStateException("远程图片为空");
        }
        Path userDir = Path.of(imagePath, username).toAbsolutePath().normalize();
        Files.createDirectories(userDir);
        String fileName = UUID.randomUUID() + "-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".png";
        Files.write(userDir.resolve(fileName), bytes);
        return "/api/images/" + username + "/" + fileName;
    }

    private OpenAiImageModel imageModel(ImageGenerationConfig config) {
        OpenAiImageApi api = new OpenAiImageApi(
                config.getApiBaseUrl(),
                new SimpleApiKey(config.getApiKey()),
                org.springframework.util.CollectionUtils.toMultiValueMap(Map.of()),
                config.getEndpointPath(),
                RestClient.builder().requestFactory(requestFactory()),
                defaultResponseErrorHandler()
        );
        return new OpenAiImageModel(api);
    }

    private OpenAiImageOptions options(ImageGenerationConfig config) {
        OpenAiImageOptions options = new OpenAiImageOptions();
        options.setModel(config.getModel());
        options.setN(1);
        options.setSize(config.getSize());
        options.setQuality(config.getQuality());
        return options;
    }

    private ResponseErrorHandler defaultResponseErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws java.io.IOException {
                return response.getStatusCode().isError();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws java.io.IOException {
                throw new IllegalStateException("OpenAI接口返回错误：" + response.getStatusCode());
            }
        };
    }

    private ClientHttpRequestFactory requestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(IMAGE_TIMEOUT)
                .withReadTimeout(IMAGE_TIMEOUT);
        return ClientHttpRequestFactories.get(settings);
    }
}
