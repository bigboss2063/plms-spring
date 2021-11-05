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

