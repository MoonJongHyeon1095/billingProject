package com.github.service;

import com.github.exception.AdErrorCode;
import com.github.exception.AdException;
import com.github.mapper.AdvertisementDetailMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

    @Mock
    private AdvertisementDetailMapper advertisementDetailMapper;
    @InjectMocks
    private AdvertisementService advertisementService;
    @BeforeEach
    void setUp() {
        // 예를 들어 필요에 따라 여기에서 기본 설정을 초기화할 수 있습니다.
    }

    //영상에 붙은 광고가 없을 떄 //아예 FeignClient로 들어왔으면 안되는 요청
    @Test
    void countAdView_whenNoAdsAttached_shouldThrowException() {
        // Setup
        int videoId = 1;
        when(advertisementDetailMapper.findAllByVideoId(videoId)).thenReturn(Collections.emptyList());

        // Execute & Verify
        Exception exception = assertThrows(AdException.class, () -> advertisementService.countAdView(videoId, 10));
        assertEquals("[ERROR] 재생된 광고가 없습니다.", exception.getMessage());
    }

    @Test
    void countAdView_withMoreViewsThanAds_shouldDistributeViewsCorrectly() {
        int videoId = 1;
        List<Integer> adPriorities = Arrays.asList(1, 2);
        when(advertisementDetailMapper.findAllByVideoId(videoId)).thenReturn(adPriorities);
        when(advertisementDetailMapper.updateViewCountByPriority(anyInt(), eq(videoId), anyInt())).thenReturn(1);

        int result = advertisementService.countAdView(videoId, 3);  // 3 views, 2 ads

        assertEquals(3, result);
        verify(advertisementDetailMapper, times(1)).updateViewCountByPriority(1, videoId, 2); // First ad gets 2 views
        verify(advertisementDetailMapper, times(1)).updateViewCountByPriority(2, videoId, 1); // Second ad gets 1 view
    }

    //
    @Test
    void countAdView_shouldThrowExceptionWhenDataIntegrityIsViolated() {
        int videoId = 1;
        List<Integer> adPriorities = Arrays.asList(1, 2);
        when(advertisementDetailMapper.findAllByVideoId(videoId)).thenReturn(adPriorities);
        when(advertisementDetailMapper.updateViewCountByPriority(anyInt(), eq(videoId), eq(1))).thenReturn(1);
        when(advertisementDetailMapper.updateViewCountByPriority(anyInt(), eq(videoId), eq(2))).thenReturn(1);  // Intentionally wrong to simulate failure

        assertThrows(AdException.class, () -> advertisementService.countAdView(videoId, 5));
    }
}
