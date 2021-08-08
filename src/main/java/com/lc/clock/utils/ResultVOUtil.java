package com.lc.clock.utils;

import com.lc.clock.vo.ResultVO;

public class ResultVOUtil {

    public static ResultVO success(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        return resultVO;
    }

    public static ResultVO success(){
        return success();
    }

    public static ResultVO error(String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(1);
        resultVO.setMsg(msg);
        return resultVO;
    }
}
