package pl.poznan.ue.matriculation.configuration

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LogAspect {

    val logger: Logger = LoggerFactory.getLogger(LogAspect::class.java)

    @Around("@annotation(pl.poznan.ue.matriculation.configuration.LogExecutionTime)")
    //@Pointcut("within(pl.poznan.ue.matriculation..*)")
    //@Pointcut("execution(* pl.poznan.ue.matriculation.local.service.*(..))")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        logger.info("Wykonuję ${joinPoint.signature.name} (${joinPoint.sourceLocation}) args: (${joinPoint.args.joinToString { "$it," }})")
        val startTime = System.nanoTime()
        val obj = joinPoint.proceed()
        val stopTime = System.nanoTime()
        val time = (stopTime - startTime) / 1000000
        logger.info("Zakończyłem wykonywanie ${joinPoint.signature.name} Time: $time ms")
        //val signature: Signature = joinPoint.signature
        //val returnType: Class<*> = (signature as MethodSignature).returnType
        return obj
    }
}