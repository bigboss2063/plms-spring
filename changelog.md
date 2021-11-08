# IOC

##  实现一个简单的Bean容器

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

## 从XML文件中加载BeanDefinition

在实现了资源加载器之后，就可以从XML文件里面读取BeanDefinition，按照Spring.xml的格式来表示BeanDefinition，读出文件中的资源后就将其注册到BeanRegistry中，这样在使用的时候我们就可以直接从容器中取出Bean而不需要再手动的注册了。

![](http://markdown.img.diamondog.online/image-20211103200513604.png)

`BeanDefinitionReader`接口，这个接口中定义了五个方法，`getRegistry()`、`getResourceLoader()`，以及三个加载Bean定义的方法。前两个方法都是提供给后面三个方法使用的工具方法，用于获取注册表和资源加载其，这个两个方法在抽象类中实现，这样就可以让具体的实现类避免污染。

```java
public interface BeanDefinitionReader {
    /**
     * 获取Bean定义的注册表
     * @return
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 获取资源加载器
     * @return
     */
    ResourceLoader getResourceLoader();

    /**
     * 根据资源加载Bean定义
     * @param resource
     * @throws BeansException
     */
    void loadBeanDefinitions(Resource resource) throws BeansException;

    /**
     * 根据多个资源加载Bean定义
     * @param resources
     * @throws BeansException
     */
    void loadBeanDefinitions(Resource... resources) throws BeansException;

    /**
     * 根据配置文件路径加载Bean定义
     * @param location
     * @throws BeansException
     */
    void loadBeanDefinitions(String location) throws BeansException;
}
```

抽象类`AbstractBeanDefinitionReader`，其中实现获取注册表和资源加载器的方法，通过构造函数将注册表和资源加载器传进去。

```java
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader{

    private final BeanDefinitionRegistry registry;

    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
```

具体实现类`XmlBeanDefinitionReader`，这个类中实现`BeanDefinitionReader`接口的后三个方法，并且有一个自己的`doLoadBeanDefinitions(InputStream inputStream)`方法，来将加载到的资源注册到Bean定义的注册表中，这里使用了hutool的XmlUtil，按标签来读取Bean，并且进行属性填充，若注册表中没有这个Bean，则将其注册。由于从xml文件中读取的内容是String类型，所以属性仅支持String类型和引用其他Bean。

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {


    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try(InputStream inputStream = resource.getInputStream()) {
            doLoadBeanDefinitions(inputStream);
        } catch (IOException e) {
            throw new BeansException("IOException parsing XML document from " + resource, e);
        }
    }

    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            loadBeanDefinitions(resource);
        }
    }

    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        Resource resource = getResourceLoader().getResource(location);
        loadBeanDefinitions(resource);
    }

    protected void doLoadBeanDefinitions(InputStream inputStream) {
        Document document = XmlUtil.readXML(inputStream);
        Element root = document.getDocumentElement();
        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }
            if (!"bean".equals(childNodes.item(i).getNodeName())) {
                continue;
            }
            Element bean = (Element) childNodes.item(i);
            String id = bean.getAttribute("id");
            String name = bean.getAttribute("name");
            String beanClassName = bean.getAttribute("class");
            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanClassName);
            } catch (ClassNotFoundException e) {
                throw new BeansException("can not find class named [" + beanClassName + "]");
            }
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(beanClass.getSimpleName());
            }
            BeanDefinition beanDefinition = new BeanDefinition(beanClass);
            for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                if (!(childNodes.item(i) instanceof Element)) {
                    continue;
                }
                if (!"property".equals(bean.getChildNodes().item(j).getNodeName())) {
                    continue;
                }
                Element property = (Element) bean.getChildNodes().item(j);
                String attrName = property.getAttribute("name");
                String attrValue = property.getAttribute("value");
                String attrRef = property.getAttribute("ref");
                if (StrUtil.isEmpty(attrName)) {
                    throw new BeansException("the name of bean can not be empty!");
                }
                Object value = attrValue;
                if (StrUtil.isNotEmpty(attrRef)) {
                    value = new BeanReference(attrRef);
                }
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("BeanName [" + beanName +"] is duplicate");
            }
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }
}
```

这一步中对BeanFactory的继承层次稍微做了y一些调整，保持和Spring中BeanFactory的继承层次一致，方便之后的功能实现。

![image-20211103204942889](http://markdown.img.diamondog.online/image-20211103204942889.png)

## 容器扩展机制BeanFactoryPostProcess和BeanPostProcessor

BeanFactoryPostProcess和BeanPostProcessor接口是Spring中非常重要的两个接口。BeanFactoryPostProcessor提供了在Bean实例化之前修改Beandefinition的能力，也就是说这个接口中的方法`void postProcessBeanFactory(ConfigurableListableBeanFactory configurableBeanFactory)`是在Bean实例化之前执行的。BeanPostProcessor提供了在Bean实例化之后能够修改或替换Bean的能力，其中定义了两个方法：在初始化之前执行的`Object postProcessBeforeInitialization(Object bean, String beanName)`和在初始化之后执行的`Object postProcessAfterInitialization(Object bean, String beanName)`。

```java
public interface BeanFactoryPostProcessor {

    /**
     * 在Bean实例化之前执行
     * @param configurableBeanFactory
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory configurableBeanFactory);
}
```

```java
public interface BeanPostProcessor {

    /**
     * 在Bean初始化之前执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在Bean初始化之后执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
```

BeanFactoryPostProcessor的执行比较简单，只要自定义实现这个接口，在实例化之前调用`void postProcessBeanFactory(ConfigurableListableBeanFactory configurableBeanFactory)`方法，参数是`ConfigurableListableBeanFactory`类，这个接口提供了分析和修改Bean以及预先实例化的操作能力。

测试：

```java
@Test
public void testBeanFactoryPostProcessor() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
        CustomBeanFactoryPostProcessor customBeanFactoryPostProcessor = new CustomBeanFactoryPostProcessor();
        // 在Bean实例化之前调用
        customBeanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
        Assert.isTrue(person.getName().equals("bigboss"));
    }
```

在`AutowireCapableBeanFactory`接口中新增两个方法，分别是用来执行所有实现了`BeanPostProcessor`接口的类中的`postProcessBeforeInitialization`和`postProcessAfterInitialization`方法。

```java
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * 执行BeanPostProcessor的postProcessBeforeInitialization方法
     * @param beanName
     * @param bean
     * @return
     */
    Object applyBeanPostProcessorsBeforeInitialization(String beanName, Object bean);

    /**
     * 执行BeanPostProcessor的postProcessAfterInitialization方法
     * @param beanName
     * @param bean
     * @return
     */
    Object applyBeanPostProcessorsAfterInitialization(String beanName, Object bean);
}
```

`AbstractAutowireCapableBeanFactory`类实现了这个接口，并且新增了方法来初始化Bean，从而可以在实例化Bean之后，调用初始化的方法，从而可以在初始化之前、之后来修改、替换Bean。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    InstantiationStrategy instantiationStrategy = new CglibSubClassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
            applyPropertyValues(beanName, bean, beanDefinition);
            // 在实例化Bean之后调用初始化方法，在这个方法中调用BeanPostProcesser的方法
            initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed");
        }
        addSingleton(beanName, bean);
        return bean;
    }

	// ...省略之前的代码

    protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(beanName, bean);

        invokeInitMethods(beanName, wrappedBean, beanDefinition);

        wrappedBean = applyBeanPostProcessorsAfterInitialization(beanName, wrappedBean);
        return wrappedBean;
    }

    protected void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 暂时还没有实现Bean的初始化
        System.out.println("执行bean[" + beanName + "]的初始化方法");
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(String beanName, Object bean) {
        Object result = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(String beanName, Object bean) {
        Object result = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
}
```

在`ConfigurableBeanFactory`接口中增加一个添加BeanPostProcesser的方法，同时，要在`AbstractBeanFactory`中增加一个List来作为保存BeanPostProcesser的容器，在初始化Bean的时候会遍历这个容器，将其中所有的BeanPostProcesser都执行一遍。

测试：

```java
@Test
public void testBeanPostProcessor() throws Exception {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
    beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");
    CustomBeanPostProcessor customerBeanPostProcessor = new CustomBeanPostProcessor();
    beanFactory.addBeanPostProcessor(customerBeanPostProcessor);
    Car car = (Car) beanFactory.getBean("car");
    System.out.println(car);
    //brand属性在CustomerBeanPostProcessor中被修改为hongqi
    Assert.isTrue(car.getBrand().equals("hongqi"));
}
```

## 实现应用上下文ApplicationContext

> 应用上下文ApplicationContext是spring中较之于BeanFactory更为先进的IOC容器，ApplicationContext除了拥有BeanFactory的所有功能外，还支持特殊类型bean如上一节中的BeanFactoryPostProcessor和BeanPostProcessor的自动识别、资源加载、容器事件和监听器、国际化支持、单例bean自动初始化等。

我们之前在测试用例中使用的`DefaultListableBeanFactory`、`XmlBeanDefinitionReader`来操作容器都是面向Spring本身的，而在用户使用的时候我们不可能让用户手动初始化工厂、读取配置文件、添加`BeanFactoryPostProcessor`和`BeanPostProcessor`，而是应该将它们封装进应用上下文中，只需要在配置文件中配置这些内容，就可以让ApplicationContext自动完成这些任务，甚至做一些额外的拓展。 

新建`ApplicationContext`，它是提供上下文功能的接口，继承自`ListableBeanFactory`，所以它有`BeanFactory`的一系列功能。

```java
public interface ApplicationContext extends ListableBeanFactory {
    //... 暂时没有定义方法
}
```

新建`ConfigurableApplicationContext`接口，继承`ApplicationContext`，并且定义一个核心方法`void refresh()`，用于刷新容器。

```java
public interface ConfigurableApplicationContext extends ApplicationContext{
    void refresh() throws BeansException;
}
```

接下来实现抽象应用上下文类，在这个抽象类中实现刷新容器的方法。因为应用上下文要实现资源加载，所以这个抽象类会继承`DefaultResourceLoader`，来处理配置文件中的BeanDefinition。并且要完成`BeanFactoryPostProcessors`的调用以及`BeanPostProcessors`的注册，同时定义`void refreshBeanFactory()`、`ConfigurableListableBeanFactory getBeanFactory()`两个抽象方法，让继承此抽象类的类完成。注意`ConfigurableListableBeanFactory`接口中添加了一个提前实例化所有单例Bean对象的方法。

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    @Override
    public void refresh() throws BeansException {
        // 刷新容器，创建 BeanFactory，并加载 BeanDefinition
        refreshBeanFactory();
        // 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        // 在Bean实例化之前调用所有的 postProcessBeanFactory方法
        invokeBeanFactoryPostProcessors(beanFactory);
        // 注册BeanPostProcessor
        registerBeanPostProcessors(beanFactory);
        // 提前实例化所有单例Bean
        beanFactory.preInstantiateSingletons();
    }

    protected abstract void refreshBeanFactory() throws BeansException;

    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }
    
    // ...getBeansOfType、getBean、getBeanDefinitionNames
}
```

在`AbstractRefreshableApplicationContext`类中完成Bean工厂获取和资源加载，并且定义抽象方法`void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)`，由继承该类的抽象类完成。

```java
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    private DefaultListableBeanFactory beanFactory;

    @Override
    protected void refreshBeanFactory() throws BeansException {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    @Override
    protected ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);
}
```

根据资源加载配置这一功能由抽象类`AbstractXmlApplicationContext`完成，它实现上一个抽象类中的抽象方法，并且在该方法中调用`XmlBeanDefinitionReader`中的`loadBeanDefinitions`方法，根据资源地址加载配置信息。定义一个方法`String[] getConfigLocations()`，用于获取资源的地址。

```java
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            xmlBeanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    protected abstract String[] getConfigLocations();
}
```

最后就是应用上下文的实现类了，在以上三个抽象类的层层剥离之下，这个实现类需要完成的功能就非常简单，只需要通过构造方法将资源地址传入并且调用`refresh()`就可以了。

```java
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    private String[] configLocations;

    public ClassPathXmlApplicationContext(String configLocation) {
        this(new String[]{configLocation});
    }

    public ClassPathXmlApplicationContext(String[] configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }
}
```

测试：

```java
public class testApplicationContext {
    @Test
    public void testApplicationContext() throws Exception {
        // 在实例化应用上下文的时候，实际上调用了refresh()方法来刷新容器
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        Person person = applicationContext.getBean("person", Person.class);
        System.out.println(person);
        //name属性在CustomBeanFactoryPostProcessor中被修改为bigboss
        assertThat(person.getName()).isEqualTo("bigboss");
        Car car = applicationContext.getBean("car", Car.class);
        System.out.println(car);
        //brand属性在CustomerBeanPostProcessor中被修改为hongqi
        assertThat(car.getBrand()).isEqualTo("hongqi");
    }
}
```

## bean的初始化和销毁方法

之前的实现中，在Bean初始化过程我们是以一个打印代替了整个初始化的过程，但现在我们来在初始化的过程中执行一些操作，比如说数据的加载执行、链接注册中心暴露RPC接口，以及在Web程序关闭时执行链接断开，内存销毁等操作，我们可以把这些操作都交给Spring容器来完成，只需要在Spring.xml中指定init-method和destroy-method，或者将Bean实现`InitializingBean`和`DisposableBean`接口。

定义InitializingBean接口。

```java
public interface InitializingBean {

    /**
     * 在Bean完成属性填充之后执行
     * @throws BeansException
     */
    void afterPropertiesSet() throws BeansException;
}
```

定义DisposableBean接口

```java
public interface DisposableBean {

    /**
     * 销毁Bean实例
     * @throws BeansException
     */
    void destroy() throws Exception;
}
```

在`BeanDefinition`中补充内容，加上initMethodName、destroyMethodName两个属性，以及相应的getter、setter。

```java
public class BeanDefinition {

    private Class<?> beanClass;

    private PropertyValues propertyValues;

    private String initMethodName;

    private String destroyMethodName;
	
    // ...setter/getter
}
```

接下来先完成Bean的初始化操作，也就是在设置完Bean属性后执行Bean中定义的初始化方法，在`AbstractAutowireCapableBeanFactory#invokeInitMethods`中执行。

```java
protected void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
    	// 判断Bean是否实现了InitializingBean，有则执行afterPropertiesSet()方法
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
        String initMethodName = beanDefinition.getInitMethodName();
    	// 判断Bean中是否有自定义的初始化方法，有则通过反射调用自定义初始化方法
        if (StrUtil.isNotEmpty(initMethodName)) {
            Method method = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (null == method) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            method.invoke(bean);
        }
    }
```

然后完成Bean的销毁操作，在`DefaultSingletonBeanRegistry`中增加属性disposableBeans，用于保存有销毁方法的Bean，添加一个`AbstractAutowireCapableBeanFactory#registerDisposableBeanIfNecessary`方法，来将有销毁方法的Bean注册到注册表中。接口`ConfigurableBeanFactory`定义了`destroySingletons`销毁方法，并由`AbstractBeanFactory`继承的父类`DefaultSingletonBeanRegistry`实现`ConfigurableBeanFactory`接口定义的 `destroySingletons`方法。

在`DefaultSingletonBeanRegistry`中实现`void destroySingletons()`、`void registerDisposableBean(String beanName, DisposableBean bean)`方法，来注册、销毁带有销毁方法的Bean。

```java
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    // ...singletonObjects

    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    // ...省略之前的代码

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    public void destroySingletons() {
        Set<String> beanNames = disposableBeans.keySet();
        for (String beanName : beanNames) {
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' throw an exception", e);
            }
        }
    }
}
```

定义销毁方法适配器，因为销毁方法有多种，目前有实现接口DisposableBean，还有在xml文件中配置destroy-method，所以这里有一个统一的接口进行销毁，在向disposableBeans中注册带有销毁方法的Bean时，就将`DisposableBeanAdapter`注册进去，销毁时统一只需调用`DisposableBeanAdapter`中的`destroy()`方法。

```java
public class DisposableBeanAdapter implements DisposableBean {

    private final Object bean;

    private final String beanName;

    private String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws Exception {
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }
        // 避免同时继承自DisposableBean，且自定义方法与DisposableBean方法同名，销毁方法执行两次的情况
        if (StrUtil.isNotEmpty(destroyMethodName) && !(bean instanceof DisposableBean && "destroy".equals(destroyMethodName))) {
            Method method = bean.getClass().getMethod(destroyMethodName);
            if (null == method) {
                throw new BeansException("Couldn't find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
            }
            method.invoke(bean);
        }
    }
}
```

接下来实现`AbstractAutowireCapableBeanFactory#registerDisposableBeanIfNecessary`方法并在初始化后调用

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    InstantiationStrategy instantiationStrategy = new CglibSubClassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
            applyPropertyValues(beanName, bean, beanDefinition);
            initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed");
        }
        // 注册有销毁方法的bean
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
        addSingleton(beanName, bean);
        return bean;
    }

	// ...省略之前的代码
    
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // Bean如果实现了DisposableBean接口或者Bean中有自定义的销毁方法，则将其注册进注册表中
        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            // 将Bean封装成DisposableBeanAdapter，注册进disposableBeans中
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }
    
	// ...省略之前的代码
}
```

这样，就已经将工厂改造完毕了，现在它具有初始化和销毁Bean的能力，但是在Spring中，我们不可能手动调用销毁方法，而是希望在虚拟机关闭之前执行销毁操作，这时候就要应用上下文来完成向虚拟机注册钩子。

先在`ConfigurableApplicationContext`接口中添加`void registerShutdownHook()`、`void close()`方法，接下来在`AbstractApplicationContext`中进行实现。

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    // ...省略之前的代码

    @Override
    public void close() {
        doClose();
    }

    @Override
    public void registerShutdownHook() {
        Thread shutdownHook = new Thread(this::doClose);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    protected void doClose() {
        destroyBeans();
    }

    protected void destroyBeans() {
        // 由于子类实现的getBeanFactory()可以获取到DefaultListableBeanFactory，它可以调用父类DefaultSingletonBeanRegistry中的销毁方法
        getBeanFactory().destroySingletons();
    }
}
```

测试：

```java
public class InitAndDestroyMethodTest {

    @Test
    public void testInitAndDestroyMethod() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:init-and-destroy-method.xml");
        applicationContext.registerShutdownHook();  //或者手动关闭 applicationContext.close();
    }
}
```

## Aware接口

Aware接口是标记性接口，他的实现子类可以感知容器的相关对象，所谓感知，就是我们可以通过实现类来获取容器相关的对象，常用的Aware接口有`BeanFactoryAware`和`ApplicationContextAware`，分别可以用来感知（获取）`BeanFactory`和`ApllicationContext`，当我们需要用到容器的能力做一些拓展的时候，Aware接口就派上用场了。

定义标记性接口`Aware`。

```java
// 标记类接口,里面不需要方法，实现该接口能感知容器类接口
public interface Aware {}
```

分别定义`BeanFactoryAware`和`ApplicationContextAware`接口，继承Aware接口，分别定义`void setBeanFactory(BeanFactory beanFactory)`和`void setApplicationContext(ApplicationContext applicationContext)`方法，用于设置实现类对应的`BeanFactory`和`ApllicationContext`。

```java
public interface ApplicationContextAware extends Aware {
	void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
```

```java
public interface BeanFactoryAware extends Aware {
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
```

自定义一个`ApplicationContextAwareProcessor`，在`AbstractApplicationContext#refresh`中将它注册进容器，这个`BeanPostProcessor`会在Bean实例化之后判断这个Bean是否实现了`ApplicationContextAware`接口，如果是则调用Bean中的`setApplicationContext`方法，将当前容器设置进Bean。

```java
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

	private final ApplicationContext applicationContext;

	public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ApplicationContextAware) {
			((ApplicationContextAware) bean).setApplicationContext(applicationContext);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
```

在`AbstractAutowireCapableBeanFactory#initializeBean`中，即初始化之前，判断Bean是否实现了`BeanFactoryAware`接口，如果是则调用Bean中的`setBeanFactory`方法，将当前工厂设置进Bean。

测试：

```java
public class AwareInterfaceTest {
    @Test
    public void testAwareInterface() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
        // 在Bean中也要添加getApplicationContext()、getBeanFactory()，来获取容器相关的对象
        assertThat(helloService.getApplicationContext()).isNotNull();
        assertThat(helloService.getBeanFactory()).isNotNull();
        applicationContext.registerShutdownHook();
    }
}
```

## 对象作用域，增加Prototype

之前我们创建的Bean全部都是单例的，但有时候Bean的需求有可能是原型模式的。在这一节中，将增加对原型模式的支持。

首先扩展`BeanDefinition`，添加如下几个属性和方法。

```java
public class BeanDefinition {
    
    //...省略之前的代码

    private String SCOPE_SINGLETON = "singleton";

    private String SCOPE_PROTOTYPE = "prototype";

    private String scope = SCOPE_SINGLETON;

    private boolean singleton = true;

    private boolean prototype = false;

    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    public boolean isSingleton() {
        return this.singleton;
    }

    public boolean isPrototype() {
        return this.prototype;
    }
	
    // ...省略之前的代码
}
```

由于xml配置文件中，bean标签上的属性又多了一个scope属性，所以需要改造xml文件资源加载其，将读出来的bean的scope填到beandefinition中。

```java
protected void doLoadBeanDefinitions(InputStream inputStream) {
        // ...省略之前的代码
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }
            if (!"bean".equals(childNodes.item(i).getNodeName())) {
                continue;
            }
            Element bean = (Element) childNodes.item(i);
            String id = bean.getAttribute("id");
            String name = bean.getAttribute("name");
            String beanClassName = bean.getAttribute("class");
            String initMethodName = bean.getAttribute("init-method");
            String destroyMethodName = bean.getAttribute("destroy-method");
            String beanScope = bean.getAttribute("scope");
            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanClassName);
            } catch (ClassNotFoundException e) {
                throw new BeansException("can not find class named [" + beanClassName + "]");
            }
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(beanClass.getSimpleName());
            }
            BeanDefinition beanDefinition = new BeanDefinition(beanClass);
            beanDefinition.setInitMethodName(initMethodName);
            beanDefinition.setDestroyMethodName(destroyMethodName);
            // 根据加载出的scope属性，调用BeanDefinition#setScope方法来设置Bean的scope值
            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }
            
            // ...省略之前的代码
            
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }
```

最后只需要在创建Bean之后，判断它是不是一个单例Bean，如果不是单例Bean就不把它放进singletonObjects中，并且也不执行销毁方法。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
		// ...
    	registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

         // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
         if (beanDefinition.isSingleton()) {
             addSingleton(beanName, bean);
         }
         return bean;
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
         // 非 Singleton 类型的 Bean 不执行销毁方法
         if (!beanDefinition.isSingleton()) return;

         if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
             registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
         }
    }
    
    // ... 其他功能
}
```

测试：

```java
public class PrototypeBeanTest {
    @Test
    public void testPrototype() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:prototype-bean.xml");
        Car car1 = applicationContext.getBean("car", Car.class);
        Car car2 = applicationContext.getBean("car", Car.class);
        // 由于配置文件中 bean标签的scope属性是prototype，所以car1和car2并不是同一个对象
        Assert.isTrue(car1 != car2);
    }
}
```
