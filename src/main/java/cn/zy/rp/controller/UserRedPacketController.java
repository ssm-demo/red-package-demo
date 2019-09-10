package cn.zy.rp.controller;

import cn.zy.rp.pojo.RedPackage;
import cn.zy.rp.service.RedPacketService;
import cn.zy.rp.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userRedPacket")
public class UserRedPacketController
{
    @Autowired
    private UserRedPacketService userRedPacketService;
    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/index")
    public String index()
    {
        return "index";
    }

    /**
     * 不作任何处理的抢红包--会出现超抢
     * @param redPacketId
     * @param uesrId
     * @return
     */
    @RequestMapping(value="/grapRedPacket")
    public ModelAndView grabRedPAcket(Long redPacketId, Long uesrId)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(new MappingJackson2JsonView());
        int result = userRedPacketService.grabRedPacket(redPacketId, uesrId);
        modelAndView.addObject("sucess", result>0);
        modelAndView.addObject("message", result>0?"抢红包成功":"抢红包失败");
        return modelAndView;
    }

    /**
     *  使用悲观锁抢红包，不会超抢，但是性能较差 会加锁
     * @param redPacketId
     * @param uesrId
     * @return
     */
    @RequestMapping(value="/grabRedPacketBypessimisticlocking")
    public ModelAndView grabRedPacketBypessimisticlocking(Long redPacketId, Long uesrId)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(new MappingJackson2JsonView());
        int result = userRedPacketService.grabRedPacketBypessimisticlocking(redPacketId, uesrId);
        modelAndView.addObject("sucess", result>0);
        modelAndView.addObject("message", result>0?"抢红包成功":"抢红包失败");
        return modelAndView;
    }


    /**
     * 使用乐观锁抢红包，通过版本比较会出现很大的失败率，需要添加重试机制-重试3次
     * @param redPacketId
     * @param uesrId
     * @return
     */
    @RequestMapping(value="/grabRedPacketByOptimisticLock")
    public ModelAndView grabRedPacketByOptimisticLock(Long redPacketId, Long uesrId)
    {
        int result = userRedPacketService.grabRedPacketByOptimisticLock(redPacketId, uesrId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(new MappingJackson2JsonView());
        modelAndView.addObject("sucess", result>0);
        modelAndView.addObject("message", result>0?"抢红包成功":"抢红包失败");
        return modelAndView;
    }


    /**
     * 使用Redis抢红包
     * @param redPacketId
     * @param uesrId
     * @return
     */
    @RequestMapping(value="/grabRedPacketByRedis")
    public ModelAndView grabRedPacketByRedis(Long redPacketId, Long uesrId)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(new MappingJackson2JsonView());
        List  list = redisTemplate.opsForHash().values("red_packet_" + redPacketId);

        if(null == list || list.size() < 1)
        {
            RedPackage redPackage = redPacketService.getRedPacket(redPacketId);
            if(null != redPackage)
            {
               redisTemplate.opsForHash().put("red_packet_" + redPacketId, "stock", redPackage.getStock() +"");
               redisTemplate.opsForHash().put("red_packet_" + redPacketId, "unit_amount",redPackage.getUnitAmount() + "");
            }
            else
            {
                modelAndView.addObject("result","no this red packet!");

                return modelAndView;
            }
        }

        Long result = userRedPacketService.grabRedPacketByRedis(redPacketId, uesrId);
        Map<String, Object> resultMap = new HashMap<>();
        modelAndView.addObject("sucess", result>0);
        modelAndView.addObject("message", result>0?"抢红包成功":"抢红包失败");
        return modelAndView;
    }



    @RequestMapping(value="/getRedPackage")
    public ModelAndView getStock(Long redPacketId)
    {
        RedPackage redPackage = redPacketService.getRedPacket(redPacketId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(redPackage);
        modelAndView.setView(new MappingJackson2JsonView());
        return modelAndView;
    }
}
