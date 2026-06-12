package com.qzcy.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzcy.backend.entity.ImageRecord;

public interface ImageService {
    ImageRecord generate(Long userId, String username, String prompt, String qualityCode);
    Page<ImageRecord> history(Long userId, long page, long size);
}
