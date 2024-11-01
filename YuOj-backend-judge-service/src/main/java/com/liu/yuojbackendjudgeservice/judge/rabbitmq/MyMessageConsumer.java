package com.liu.yuojbackendjudgeservice.judge.rabbitmq;

import com.liu.yuojbackendjudgeservice.judge.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author 刘渠好
 * @since 2024-11-01 23:29
 */
@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;
    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    //ackMode = "MANUAL" 手动确认还是自动确认
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId= Long.parseLong (message);
        try {
            //消费消息
            judgeService.doJudge (questionSubmitId);
            channel.basicAck (deliveryTag,false);
        }catch (Exception e){
            //消费失败处理策略 requeue：是否将消息再次放进队列中进行消费
            channel.basicNack (deliveryTag,false,false);
        }

        channel.basicAck(deliveryTag, false);
    }

}
