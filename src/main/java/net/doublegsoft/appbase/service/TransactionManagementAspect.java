/*
 * Copyright 2016 doublegsoft.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.doublegsoft.appbase.service;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;

import net.doublegsoft.appbase.service.AbstractService;

/**
 *
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 *
 * @since 1.0
 */
@Aspect
public class TransactionManagementAspect implements MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice {

    /**
     * @see org.springframework.aop.MethodBeforeAdvice#before(java.lang.reflect.Method, java.lang.Object[],
     *      java.lang.Object)
     */
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {

    }

    /**
     * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object, java.lang.reflect.Method,
     *      java.lang.Object[], java.lang.Object)
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {

    }

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void beginTransaction(JoinPoint joinPoint) {
        AbstractService service = (AbstractService) joinPoint.getTarget();
        if (!service.isAutoTransactionManagement()) {
            return;
        }
        service.begin();
    }

    @AfterReturning("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void commit(JoinPoint joinPoint) {
        AbstractService service = (AbstractService) joinPoint.getTarget();
        if (!service.isAutoTransactionManagement()) {
            return;
        }
        service.commit();
    }

    @AfterThrowing("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void rollback(JoinPoint joinPoint) {
        AbstractService service = (AbstractService) joinPoint.getTarget();
        if (!service.isAutoTransactionManagement()) {
            return;
        }
        service.rollback();
    }

}
