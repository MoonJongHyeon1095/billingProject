package mapper;

import com.github.config.db.MyBatisConfig;
import com.github.mapper.AdvertisementDetailMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes = MyBatisConfig.class)
@MapperScan("com.github.mapper")
public class AdvertisementDetailMapperTest {
    @Autowired
    private AdvertisementDetailMapper advertisementDetailMapper;

    @Test
    public void countByVideoIdTest() {
        Integer count = advertisementDetailMapper.countByVideoId(1);
        assertEquals(2, count);
    }

    @Test
    public void findAllByVideoIdTest() {
        List<Integer> priorities = advertisementDetailMapper.findAllByVideoId(1);
        assertEquals(2, priorities.size());
        assertTrue(priorities.containsAll(Arrays.asList(1, 2)));

    }

    @Test
    public void updateViewCountByPriorityTest() {
        int updated = advertisementDetailMapper.updateViewCountByPriority(1, 1, 50);
        assertEquals(1, updated);

        Integer count = advertisementDetailMapper.countByVideoId(1);
        assertEquals(2, count);
    }
}
