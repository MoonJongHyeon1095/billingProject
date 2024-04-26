package com.github.service;

import com.github.exception.AdErrorCode;
import com.github.exception.AdException;
import com.github.mapper.AdvertisementDetailMapper;
import com.github.mapper.AdvertisementMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  광고 재생횟수를 Advertisement 테이블과 AdvertisementDetail 테이블에 반영
 *  AdDetail 테이블에는 해당 Video에 달린 광고들을 순회하며 누적 업데이트
 *  Ad 테이블에는 dailyIncrement 으로 저장, 이후 Batch 작업을 통해 AdDetail viewCount의 adPriority별 누적합과 대조 후 최종 업데이트
 */
@Service
@AllArgsConstructor
public class AdvertisementService {
    private final AdvertisementDetailMapper advertisementDetailMapper;
    private final AdvertisementMapper advertisementMapper;

    @Transactional
    public void countAdView( final int videoId, final int adViewCount) {
        //해당 videoId의 영상에 5분 간격으로 광고가 몇개 붙어있는지
        List<Integer> adList = getAllAdByVideoId(videoId);
        int plusViewCount=0;
        int remainingViewCount=0;
        int attachedAdNum = adList.size();

        if(adViewCount==0 || attachedAdNum==0) throw new AdException(AdErrorCode.AD_NOT_FOUND);

        //1시간 짜리 영상에 12개의 광고가 있고, adViewCount가 27회라면 일단 모든 광고 조회수 +2
        //이후 나머지만큼 오름차순 정렬된 adPriority(광고 우선순위) 순회하며 조회수 +1
        plusViewCount = adViewCount/ attachedAdNum;
        remainingViewCount = adViewCount % attachedAdNum;

        //1. 1시간 짜리 영상에 12개의 광고가 있고, adViewCount가 27회라면 일단 모든 광고 조회수 +2
        updateAdDetailViewsByPriority(adList, videoId, plusViewCount);
        //updateAdIncrementByPriority(adList, plusViewCount);
        //2. 이후 나머지만큼 오름차순 정렬된 adPriority(광고 우선순위) 순회하며 조회수 +1
        if(remainingViewCount > 0) {
            List<Integer> remainingAds = adList.subList(0, remainingViewCount);
            updateAdDetailViewsByPriority(remainingAds, videoId, 1);
            //updateAdIncrementByPriority(remainingAds, 1);
        }
    }

    //Advertisement 테이블 dailyIncrement 칼럼 업데이트
    private void updateAdIncrementByPriority(final List<Integer> adPriorities, final int increment){
        for (int adPriority : adPriorities) {
            advertisementMapper.updateDailyIncrement(adPriority, increment);
        }
    }

    //AdvertisementDetail 테이블 viewCount 칼럼 업데이트
    private void updateAdDetailViewsByPriority(final List<Integer> adPriorities,final int videoId, final int plusView) {
        for (int adPriority : adPriorities) {
            advertisementDetailMapper.updateViewCountByPriority(adPriority, videoId, plusView);
        }
    }
    private List<Integer> getAllAdByVideoId(final int videoId){
        return advertisementDetailMapper.findAllByVideoId(videoId);
    }
}
