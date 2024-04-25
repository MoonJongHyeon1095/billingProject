package com.github.config.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.StepExecution;

public class ChunkListenerImpl implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        // Chunk 시작 전 설정할 값이 있는 경우
    }

    @Override
    public void afterChunk(ChunkContext context) {
        // Chunk가 완료된 후 ExecutionContext에 값 추가
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        // Chunk에서 오류가 발생한 후 설정할 값이 있는 경우
    }
}


