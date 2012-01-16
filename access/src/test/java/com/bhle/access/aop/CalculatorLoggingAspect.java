package com.bhle.access.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class CalculatorLoggingAspect {

	private static final Logger logger = LoggerFactory
			.getLogger(CalculatorLoggingAspect.class);

	// @Before("execution(* UnitCalculator.kilogramToPound(..))")
	// public void logBefore(JoinPoint joinPoint) {
	// logger.info("The method " + joinPoint.getSignature().getName()
	// + "() begins with " + Arrays.toString(joinPoint.getArgs()));
	// }
	//
	// @AfterReturning(pointcut = "execution(* *.*(..))", returning = "result")
	// public void logAfterReturning(JoinPoint joinPoint, Object result) {
	// logger.info("The method " + joinPoint.getSignature().getName()
	// + "() ends with " + result);
	// }

	@Around("execution(* *.*(..))")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info("The method " + joinPoint.getSignature().getName()
				+ "() begins with " + Arrays.toString(joinPoint.getArgs()));
		try {
			Object result = joinPoint.proceed();
			logger.info("The method " + joinPoint.getSignature().getName()
					+ "() ends with " + result);
			return result;
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument "
					+ Arrays.toString(joinPoint.getArgs()) + " in "
					+ joinPoint.getSignature().getName() + "()");
			throw e;
		}
	}
}
