package com.qzcy.backend.config;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qzcy.backend.entity.ImageRecord;
import com.qzcy.backend.mapper.ImageRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingImageCleanupRunner implements ApplicationRunner {
    private final ImageRecordMapper imageRecordMapper;

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime staleBefore = LocalDateTime.now().minusMinutes(30);
        int updated = imageRecordMapper.update(
                null,
                new LambdaUpdateWrapper<ImageRecord>()
                        .eq(ImageRecord::getStatus, "pending")
                        .lt(ImageRecord::getCreatedAt, staleBefore)
                        .set(ImageRecord::getStatus, "failed")
        );
        if (updated > 0) {
            log.warn("已清理启动前遗留的超时pending图像任务，count={}", updated);
        }
    }
}
