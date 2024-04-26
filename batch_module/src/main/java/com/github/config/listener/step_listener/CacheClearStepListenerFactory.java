package com.github.config.listener.step_listener;

import com.github.config.processor.statistic.DailyStatisticsProcessor;
import com.github.config.processor.statistic.MonthlyStatisticsProcessor;
import com.github.config.processor.statistic.WeeklyStatisticsProcessor;

/**
 * 팩토리 패턴:
 * 객체 생성 로직을 캡슐화 하는 디자인 패턴. 객체의 생성 과정을 분리, 코드의 유연성을 높이면서 객체 생성에 대한 제어를 유지.
 * - 객체 생성의 추상화: 객체 생성 로직을 팩토리로 추상화하여 클라이언트가 객체 생성 방법이나 구현 세부 사항을 알 필요가 없다.
 * - 유연한 객체 생성: 팩토리 패턴을 사용하면 특정 조건이나 입력값에 따라 다른 종류의 객체를 생성할 수 있다.
 *
 * 종류
 * - 단순 팩토리(Simple Factory): 객체 생성에 대한 책임을 별도의 팩토리 클래스에 두는 패턴. 클라이언트 코드가 객체 생성 로직을 몰라도 객체를 생성.
 * - 팩토리 메서드(Factory Method): 객체 생성에 대한 책임을 서브클래스에 두는 패턴. 클라이언트는 팩토리 메서드를 통해 객체를 생성하지만, 어떤 객체가 생성될지는 서브클래스에 의해 결정.
 * - 추상 팩토리(Abstract Factory): 객체 생성에 대한 책임을 팩토리 인터페이스에 두는 패턴. 여러 팩토리를 추상화하여 클라이언트가 원하는 팩토리의 구현체를 선택하고 객체를 생성.
 *
 * ### 팩토리 패턴의 특징
 * - **유연한 객체 생성**: 팩토리 패턴은 조건에 따라 다른 객체를 생성할 수 있어 코드의 유연성을 높입니다.
 * - **의존성 역전**: 팩토리 패턴을 사용하면 객체 생성과 사용을 분리할 수 있어 의존성 역전 원칙(DIP)을 지킬 수 있습니다.
 * - **객체 생성 로직의 캡슐화**: 팩토리 패턴은 객체 생성 로직을 캡슐화하여 코드 변경 시 영향력을 줄입니다.
 *
 * ### 예제 설명
 * 위의 팩토리 패턴 예제에서, 다양한 조합의 의존성을 가진 `CacheClearStepListener` 객체를 생성할 수 있습니다. 이는 클라이언트 코드가 각 의존성에 대한 세부 정보를 몰라도 필요한 객체를 쉽게 생성할 수 있도록 합니다. 또한 특정 의존성을 주입하는 데 유연성을 제공하여, 팩토리 패턴의 장점을 보여줍니다.
 */
public class CacheClearStepListenerFactory {
    public static  CacheClearStepListener create(
            final DailyStatisticsProcessor dailyProcessor,
            final WeeklyStatisticsProcessor weeklyProcessor,
            final MonthlyStatisticsProcessor monthlyProcessor) {
        return new CacheClearStepListener(dailyProcessor, weeklyProcessor, monthlyProcessor);
    }

    public static CacheClearStepListener createWithDaily(final DailyStatisticsProcessor dailyProcessor) {
        return new CacheClearStepListener(dailyProcessor, null, null);
    }

    public static CacheClearStepListener createWithWeekly(final WeeklyStatisticsProcessor weeklyProcessor) {
        return new CacheClearStepListener(null, weeklyProcessor, null);
    }

    public static CacheClearStepListener createWithMonthly(final MonthlyStatisticsProcessor monthlyProcessor) {
        return new CacheClearStepListener(null, null, monthlyProcessor);
    }
}
