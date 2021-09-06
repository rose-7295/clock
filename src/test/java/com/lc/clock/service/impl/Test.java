package com.lc.clock.service.impl;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 柴柴快乐每一天
 * @create 2021-09-06  10:23 上午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
public class Test {

    @org.junit.Test
    public void testAdd() {
        //test data
        int num= 5;
        String temp= null;
        String str= "Junit is working fine";

        //check for equality
        assertEquals("Junit is working fine", str);

        //check for false condition
        assertFalse(num > 6);

        //check for not null value
        assertNotNull(str);
    }
}
