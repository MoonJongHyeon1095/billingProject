package com.github.config.partition;

import com.github.dto.VideoRangeDto;
import com.github.mapper.VideoMapper;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ExecutionContext
  Spring Batch  작업 분할(partitioning)의 컨텍스트 정보를 관리.
  각 파티션에 대한 정보를 담는 ExecutionContext는 실행 컨텍스트로서
  그 파티션 내에서 수행되어야 할 작업의 특정 범위나 상태 등을 저장하는 데 사용.
  이렇게 하면 각 파티션의 실행이 독립적으로 관리

  작업의 상태 관리:
  - 각 파티션의 실행 상태를 ExecutionContext에 저장.
  - 어떤 파티션의 처리가 완료되었는지, 어디서부터 다시 시작해야 하는지 등의 정보를 저장.

 파티션별 매개변수 전달:
 - 다양한 파티션에서 실행되는 작업이 각기 다른 매개변수를 필요로 할 때 ExecutionContext를 통해 이를 전달.
 - 각 파티션의 시작 및 끝 ID와 같은 정보를 저장, 해당 파티션에서 처리해야 할 데이터 범위를 명확하게 지정.

 병렬 처리와 재시작성:
 - 배치 작업이 실패했을 때 특정 파티션에서만 재시작할 수 있도록.
 - 이는 ExecutionContext에 저장된 상태 정보를 기반으로, 실패한 파티션의 작업을 정확히 그 지점부터 다시 시작할 수 있게.

 효율적인 자원 관리:
 - 각 파티션에 대한 정보를 분리, 필요한 자원(예: 데이터베이스 커넥션, 파일 핸들 등)을 효율적으로 관리하고 할당. 대규모 데이터 처리 자원 사용량을 최적화.
 */
@Service
public class RangePartitioner implements Partitioner {
    private final VideoMapper videoMapper;

    public RangePartitioner(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        VideoRangeDto videoRangeDto = findFirstAndLastVideoId();
        final int firstVideoId = videoRangeDto.getFirstVideoId();
        final int lastVideoId = videoRangeDto.getLastVideoId();
        Map<String, ExecutionContext> result = new HashMap<>();

        int range = (lastVideoId - firstVideoId) / gridSize;
        int start = firstVideoId;
        int end = start + range - 1;

        while (start <= lastVideoId) {
            ExecutionContext value = new ExecutionContext();

            // 마지막 파티션의 경우 end를 lastVideoId 설정, 마지막 videoId까지 읽기 보장
            if(end >= lastVideoId) {
                end = lastVideoId;
            }

            value.putInt("startVideoId", start);
            value.putInt("endVideoId", end);

            // 파티션 이름 설정
            result.put("partition" + start + '_' + end, value);

            start += range;
            end += range;
        }

        return result;
    }

    private VideoRangeDto findFirstAndLastVideoId(){
        return videoMapper.findFirstAndLastVideoId();
    }
}
