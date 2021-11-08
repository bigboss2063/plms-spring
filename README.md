# plms-spring

## 关于

plms-spring一个简化版的spring，具有Spring的基本功能，如IoC和AOP、资源加载器、事件监听器、类型转换、容器扩展点、bean生命周期和作用域、应用上下文等。用于学习Spring的核心原理和核心逻辑，可以在看源码的时候更快上手。参考了[mini-spring](https://github.com/DerekYRC/mini-spring)完成实现，通过mini-spring中的测试用例追代码可以很好的学习，但是大佬没有给出详细的实现过程的文档教程，所以在实现的同时记录下来了每一步的过程。

## 功能

- [IoC](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#ioc)
    - [x] [实现一个简单的容器](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E5%AE%9E%E7%8E%B0%E4%B8%80%E4%B8%AA%E7%AE%80%E5%8D%95%E7%9A%84bean%E5%AE%B9%E5%99%A8)

    - [x] [实现Bean的定义、注册、获取](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E5%AE%9E%E7%8E%B0bean%E7%9A%84%E5%AE%9A%E4%B9%89%E6%B3%A8%E5%86%8C%E5%92%8C%E8%8E%B7%E5%8F%96)

    - [x] [Bean实例化策略InstantiationStrategy](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#bean%E5%AE%9E%E4%BE%8B%E5%8C%96%E7%AD%96%E7%95%A5instantiationstrategy)

    - [x] [为Bean注入属性和依赖Bean](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E4%B8%BAbean%E6%B3%A8%E5%85%A5%E5%B1%9E%E6%80%A7%E5%92%8C%E4%BE%9D%E8%B5%96bean)

    - [x] [资源和资源加载器](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E8%B5%84%E6%BA%90%E5%92%8C%E8%B5%84%E6%BA%90%E5%8A%A0%E8%BD%BD%E5%99%A8)

    - [x] [从XML文件中加载BeanDefinition](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E4%BB%8Exml%E6%96%87%E4%BB%B6%E4%B8%AD%E5%8A%A0%E8%BD%BDbeandefinition)
    
    - [x] [容器扩展机制BeanFactoryPostProcess和BeanPostProcessor](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E5%AE%B9%E5%99%A8%E6%89%A9%E5%B1%95%E6%9C%BA%E5%88%B6beanfactorypostprocess%E5%92%8Cbeanpostprocessor)

    - [x] [应用上下文ApplicationContext](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#%E5%AE%9E%E7%8E%B0%E5%BA%94%E7%94%A8%E4%B8%8A%E4%B8%8B%E6%96%87applicationcontext)

    - [x] [Bean的初始化和销毁方法](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#bean%E7%9A%84%E5%88%9D%E5%A7%8B%E5%8C%96%E5%92%8C%E9%94%80%E6%AF%81%E6%96%B9%E6%B3%95)

    - [x] [Aware接口](https://github.com/bigboss2063/plms-spring/blob/main/changelog.md#aware%E6%8E%A5%E5%8F%A3)

    - [ ] Bean作用域，增加prototype支持

    - [ ] FactoryBean

    - [ ] 容器事件和事件监听器
- AOP  
    - [ ] 切点表达式
    - [ ] 基于JDK的动态代理
    - [ ] 基于CGLIB的动态代理
    - [ ] AOP代理工厂ProxyFactory
    - [ ] 几种常用的Advice: BeforeAdvice/AfterAdvice/AfterReturningAdvice/ThrowsAdvice
    - [ ] PointcutAdvisor：Pointcut和Advice的组合
    - [ ] 动态代理融入bean生命周期
- 拓展
    - [ ] PropertyPlaceholderConfigurer
    - [ ] 包扫描
    - [ ] @Value注解
    - [ ] 基于注解@Autowired的依赖注入
    - [ ] 类型转换
    - [ ] 解决循环依赖

## 参考

- [mini-spring](https://github.com/DerekYRC/mini-spring)
