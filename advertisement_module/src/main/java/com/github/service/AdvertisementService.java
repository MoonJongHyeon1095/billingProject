package com.github.service;

import com.github.exception.AdErrorCode;
import com.github.exception.AdException;
import com.github.mapper.AdvertisementDetailMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  광고 재생횟수를 AdvertisementDetail 테이블에 반영
 *
 *  TODO: 광고재생횟수 검증
 *  AdDetail 테이블의 각 videoId 광고재생 횟수 합산, 영상(Video) 테이블 혹은 영상통계(VideoStatistic)테이블에 업데이트
 *  통계 및 정산 batch 작업시 대조
 */
@Service
@AllArgsConstructor
public class AdvertisementService {
    private final AdvertisementDetailMapper advertisementDetailMapper;

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
        //2. 이후 나머지만큼 오름차순 정렬된 adPriority(광고 우선순위) 순회하며 조회수 +1
        if(remainingViewCount > 0) {
            List<Integer> remainingAds = adList.subList(0, remainingViewCount);
            updateAdDetailViewsByPriority(remainingAds, videoId, 1);
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
