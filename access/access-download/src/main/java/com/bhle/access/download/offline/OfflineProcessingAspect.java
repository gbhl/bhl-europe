package com.bhle.access.download.offline;

import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class OfflineProcessingAspect {
	
	private static final Logger logger = LoggerFactory
			.getLogger(OfflineProcessingAspect.class);
	
	@Around("@annotation(com.bhle.access.download.offline.Offlinable)")
	public Object offlineProcessingAround(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info("The method " + joinPoint.getSignature().getName()
				+ "() begins with " + Arrays.toString(joinPoint.getArgs()));
		String methodName = joinPoint.getSignature().getName().toLowerCase();
		
		try {
			if (!CanBeRealTime()){
				RawRequest rawRequst = new RawRequest(joinPoint.getArgs());
				if (methodName.endsWith("pdf")){
					rawRequst.setFormat("pdf");
				} else if (methodName.endsWith("jpeg") || methodName.endsWith("jpg") ){
					rawRequst.setFormat("jpg");
				} else {
					throw new IllegalArgumentException("Unkown format, please check the method name suffix");
				}
//				return Response.ok(new Viewable("/email", rawRequst)).build();
				return null;
			} else {
				Object result = joinPoint.proceed();
				return result;
			}
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument "
					+ Arrays.toString(joinPoint.getArgs()) + " in "
					+ joinPoint.getSignature().getName() + "()");
			throw e;
		}
	}

	private boolean CanBeRealTime() {
//		return !SystemManagementUtil.isBusy();
		return true;
	}
}
