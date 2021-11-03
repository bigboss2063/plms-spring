# IOC

##  实现一个简单的Bean容器

> 分支：simple-bean-container

要实现一个Bean容器，首先要知道Bean容器是什么。它是一个用于承载对象的容器，程序员只需要告诉Bean容器每个Bean对象是如何创建的，这之后程序员不需要再关心该Bean的创建细节和该Bean的相关依赖，一切都由容器来完成，当需要用到某个Bean时直接去容器中取即可。

定义一个类`BeanFactory`作为Bean容器，内部包含一个`HashMap`保存Bean，再定义`registerBean(String beanName, Object bean)`和`getBean(String beanName)`两个方法。

```java
public class BeanFactory {
    private Map<String, Object> beanMap = new HashMap<>();

    public void registerBean(String beanName, Object bean) {
        beanMap.put(beanName, bean);
    }

    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }
}
```

## 实现Bean的定义、注册和获取

> 分支：bean-definition-and-bean-definition-registry

上一步中实现的Bean容器，仅仅是一个普通容器，并没有体现到IOC的思想，IOC即控制反转，指的是将控制对象的权力从程序员转到IOC容器手中，它能够解决对象之间耦合的问题，不需要每次使用通过`new`来创建，而是从容器中获取，达到一种松耦合的目的。显然，上一步我们仍然需要先`new`一个对象再将其放进容器中。这一步来做出改进，我们将Bean的创建交由容器来处理，要这么做我们就要在Bean注册的时候只注册一个类信息，在`BeanDefinition`类中保存Bean的类型，将Bean的创建在获取Bean时处理。

![image-20211102133112151](http://markdown.img.diamondog.online/image-20211102133112151.png)

`BeanDefinition`定义

```java
public class BeanDefinition {

    private Class<?> beanClass;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    // ...get/set
}
```

首先将`BeanFactory`类修改为一个接口，并且定义一个获取Bean的方法`getBean(String beanName)`。

```java
public interface BeanFactory {
    /**
     * 获取bean实例
     * @param beanName bean名称
     * @return bean实例
     */
    Object getBean(String beanName);
}
```

`BeanFactory`接口将由抽象类`AbstractBeanFactory`实现，通过模板模式的设计思想，隐藏具体获取Bean对象的细节，具体的获取步骤由其子类实现。

```java
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    @Override
    public Object getBean(String beanName) {
        Object bean = getSingleton(beanName);
        if (bean == null) {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            bean = createBean(beanName, beanDefinition);
            addSingleton(beanName, bean);
        }
        return bean;
    }

    /**
     * 获取bean定义
     * @param beanName bean名称
     * @return bean定义
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName);

    /**
     * 新建bean
     * @param beanName
     * @param beanDefinition
     * @return
     */
    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition);
}
```

添加一个`AbstractAutowireCapableBeanFactory`类继承`AbstractBeanFactory`类，实现`createBean(String beanName, BeanDefinition beanDefinition)`方法，由于它也是一个抽象类，所以`getBeanDefinition(String beanName)`方法再由继承它的子类实现。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = null;
        try {
            bean = beanDefinition.getBeanClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeansException("create bean[" + beanName + "] failed");
        }
        return bean;
    }
}
```

由于有些对象是单例对象，所以第二次获取该对象时，我们只需要在内存中获取即可，所以要实现一个Bean对象的注册表来保存这些单例对象。

先定义一个`SingletonBeanRegistry`接口。

```java
public interface SingletonBeanRegistry {

    /**
     * 获取单例bean实例
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);
}
```

定义`DefaultSingletonBeanRegistry`类实现这个接口，在这个类中声明一个`HashMap`作为注册表来保存这些单例对象，并实现一个`addSingleton(String beanName, Object bean)`方法来将Bean对象添加到注册表中。`AbstractBeanFactory`类需要继承这个类，那么这个抽象工厂就同时拥有获取Bean和注册Bean的能力了。

```java
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry  {

    private Map<String, Object> singletonObjects = new HashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }

    public void addSingleton(String beanName, Object bean) {
        singletonObjects.put(beanName, bean);
    }
}
```

因为我们现在注册Bean的时候是先将Bean的定义注册，当要获取Bean的时候，才拿着对应Bean的信息去获取实例，那么显然需要一个BeanDefinition的注册表。

```java
public interface BeanDefinitionRegistry {
    /**
     * 注册bean定义
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
```

接下来是核心实现类`DefaultListableBeanFactory `，它继承了`AbstractAutowireCapableBeanFactory `类所以有父类的所有功能，并且实现了`BeanDefinitionRegistry`接口，实现了`registerBeanDefinition(String beanName, BeanDefinition beanDefinition)`方法。

```JAVA
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    @Override
    protected BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("no beanDefinition named [" + beanName + "]");
        }
        return beanDefinition;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }
}
```

## Bean实例化策略InstantiationStrategy

> 分支：bean-instantiation-strategy

现在Bean的创建是由`AbstractAutowireCapableBeanFactory.createBean(String beanName, BeanDefinition beanDefinition)`来创建的，使用了`beanDefinition.getBeanClass().newInstance();`来实例化，但是这种创建方式只适用于无参构造函数的情况。抽象出一个实例化策略的接口`InstantiationStrategy`，以及补充相应的`getBean`入参信息，让外部调用时可以传递构造函数的入参并顺利实例化。

![image-20211102174039869](http://markdown.img.diamondog.online/image-20211102174039869.png)

BeanFactory中新增`Object getBean(String beanName, Object... args)`，可以填入参数。

```java
public interface BeanFactory {
    /**
     * 获取bean实例
     * @param beanName bean名称
     * @return bean实例
     */
    Object getBean(String beanName);

    /**
     * 在有参构造函数的情况下获取bean实例
     * @param beanName bean名称
     * @param args 参数
     * @return bean实例
     */
    Object getBean(String beanName, Object... args);
}
```

`InstantiationStrategy`接口中定义了`Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args)`方法，用于实例化Bean，其中`Constructor `类的参数是为了拿到构造函数的形参类型数组。

```java
public interface InstantiationStrategy {

    /**
     * 实例化方法
     * @param beanDefinition bean定义
     * @param beanName bean名称
     * @param constructor 构造函数
     * @param args 参数列表
     * @return bean对象
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args);
}
```

JDK实例化，判断参数`constructor`是否为空，若不为空则根据构造函数参数的类型获取对应的构造函数，将参数传入并实例化对象。

```java
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        try {
            if (constructor != null) {
                return beanClass.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(args);
            }
            return beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new BeansException("Failed to instantiate [" + beanName + "]");
        }
    }
}
```

Cglib实例化，判断参数`constructor`是否为空，若为空则直接实例化对象，否则根据构造函数参数类型，进行实例化。

```java
public class CglibSubClassingInstantiationStrategy implements InstantiationStrategy{
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
        if (constructor == null) {
            return enhancer.create();
        }
        return enhancer.create(constructor.getParameterTypes(), args);
    }
}
```

调用实例化策略，原本的实例化是在`AbstractAutowireCapableBeanFactory`类中实例化，现在新增一个`Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args)`方法来进行Bean实例化。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    InstantiationStrategy instantiationStrategy = new CglibSubClassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeansException("Instantiation of bean failed");
        }
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructor = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 获取Bean类的所有构造函数
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        for (Constructor<?> ctor : constructors) {
            /// 为了简略代码只根据参数列表的长度来选择构造函数，实际上还应该比较类型
            if (args != null && ctor.getParameterTypes().length == args.length) {
                constructor = ctor;
                break;
            }
        }
        // 用对应的构造函数进行实例化，并返回实例化对象
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructor, args);
    }

    protected InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }
}
```

## 为Bean注入属性和依赖Bean

> 分支：inject-attribute-and-rely-for-bean

Bean实例化的最后一步，我们应该考虑Bean有无属性的问题，如果类中有属性，就应该在注册Bean定义时，将属性保存起来，并且在实例化时将属性注入到Bean实例中去。

新建`PropertyValue`类，里面有两个属性`name`和`value`分别对应Bean属性的名称和值

```java
public class PropertyValue {
    private final String name;

    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }
	// ...get/set
}
```

再添加一个`PropertyValues`类，类中有一个列表，用于保存Bean的所有`PropertyValue`，并实现`addPropertyValue(PropertyValue propertyValue)`、`PropertyValue[] getPropertyValues()`、`PropertyValue getPropertyValue(String propertyName)`方法。

```java
public class PropertyValues {
    private final List<PropertyValue> propertyValues = new ArrayList<>();

    public void addPropertyValue(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValues.toArray(new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue propertyValue : this.propertyValues) {
            if (propertyValue.getName().equals(propertyName)) {
                return propertyValue;
            }
        }
        return null;
    }
}
```

接下来完善`BeanDefinition`类，添加一个`PropertyValues`类型的属性，用于保存Bean的属性。

```java
public class BeanDefinition {

    private Class<?> beanClass;

    private PropertyValues propertyValues;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.propertyValues = new PropertyValues();
    }

    public BeanDefinition(Class<?> beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }

    // ...set/get
}
```

在创建实例之后，给Bean实例注入属性

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    InstantiationStrategy instantiationStrategy = new CglibSubClassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
            applyPropertyValues(beanName, bean, beanDefinition);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeansException("Instantiation of bean failed");
        }
        return bean;
    }

    // ...createBeanInstance()

    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();
                // 如果value是 BeanReference类型则是当前所实例化bean的依赖bean
                if (value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Failed to set property in" + beanName);
        }
    }

    // ...getInstantiationStrategy()
}
```

## 资源和资源加载器

> 分支：read-bean-from-xml

上一步我们已经完全实现了一个Bean的定义、注册、属性填充、获取，但是目前我们需要手动的创建，接下来应该对其进行优化让它能够自动通过配置文件的读取，自动创建Bean。

![image-20211102211617638](http://markdown.img.diamondog.online/image-20211102211617638.png)

首先创建一个Resource接口，在其中定义一个`InputStream getInputStream()`来获取资源，分别实现三个类来从ClassPath、系统文件、云配置文件读取资源。

```java
public interface Resource {
    /**
     * 获取输入流
     * @return 输入流
     * @throws IOException io异常
     */
    InputStream getInputStream() throws IOException;
}
```

新建`ClassPathResource`类实现`Resource`接口，通过类加载器加载ClassPath中的资源。

```java
public class ClassPathResource implements Resource{

    private final String path;

    public ClassPathResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(
                    this.path + " cannot be opened because it does not exist");
        }
        return is;
    }
}
```

新建FileSystemResource类实现`Resource`接口，通过文件路径来获取文件中的资源。

```java
public class FileSystemResource implements Resource{

    private final String path;

    public FileSystemResource(String path) {
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            Path path = new File(this.path).toPath();
            return Files.newInputStream(path);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
}
```

新建UrlResource类来实现`Resource`接口，从云配置文件中读取资源。

```java
public class UrlResource implements Resource{

    private final URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection urlConnection = url.openConnection();
        try {
            return urlConnection.getInputStream();
        } catch (IOException e) {
            if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
            throw e;
        }
    }
}
```

创建一个接口`ResourceLoader`，并在其中定义一个方法`Resource getResource(String location)`来获取资源加载器。

```java
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 获取资源
     * @param location 资源地址
     * @return 资源
     */
    Resource getResource(String location);
}
```

新建`DefaultResourceLoader`类实现`ResourceLoader`接口，重写`Resource getResource(String location)`方法， 根据资源路径判断，应该返回哪个资源加载器来加载资源。

```java
public class DefaultResourceLoader implements ResourceLoader {

   public static final String CLASSPATH_URL_PREFIX = "classpath:";

   @Override
   public Resource getResource(String location) {
      if (location.startsWith(CLASSPATH_URL_PREFIX)) {
         //classpath下的资源
         return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
      } else {
         try {
            //尝试当成url来处理
            URL url = new URL(location);
            return new UrlResource(url);
         } catch (MalformedURLException ex) {
            //当成文件系统下的资源处理
            return new FileSystemResource(location);
         }
      }
   }
}
```

测试：

```java
public class ResourceTest {

    private DefaultResourceLoader resourceLoader;

    @Before
    public void init() {
        resourceLoader = new DefaultResourceLoader();
    }

    @Test
    public void classPathResourceTest() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:important.property");
        InputStream inputStream = resource.getInputStream();
        String s = IoUtil.readUtf8(inputStream);
        System.out.println(s);
    }

    @Test
    public void filePathResourceTest() throws IOException {
        Resource resource = resourceLoader.getResource("src/test/resources/important.property");
        InputStream inputStream = resource.getInputStream();
        String s = IoUtil.readUtf8(inputStream);
        System.out.println(s);
    }

    @Test
    public void urlPathResourceTest() throws IOException {
        Resource resource = resourceLoader.getResource("https://github.com/DerekYRC/mini-spring/blob/main/README.md");
        InputStream inputStream = resource.getInputStream();
        String s = IoUtil.readUtf8(inputStream);
        System.out.println(s);
    }
}
```