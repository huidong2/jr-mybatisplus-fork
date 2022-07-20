package online.githuboy.jrebel.mybatisplus;

import online.githuboy.jrebel.mybatisplus.cbp.*;
import org.zeroturnaround.javarebel.*;

import java.io.File;
import java.io.IOException;

/**
 * Plugin Main entry
 *
 * @author suchu
 * @author andresluuk
 * @since 2019/5/9 17:56
 */
public class MybatisPlusPlugin implements Plugin {
    private static final Logger log = LoggerFactory.getLogger("MyBatisPlus");
    private final static String MP_MARK_NAME = ".mybatisplus-jr-mark_";
    private final static File mp_mark = new File(MP_MARK_NAME);

    @Override
    public void preinit() {
        log.infoEcho("Ready config JRebel MybatisPlus plugin...");
        ClassLoader classLoader = MybatisPlusPlugin.class.getClassLoader();
        Integration integration = IntegrationFactory.getInstance();
        //register class processor
        configMybatisPlusProcessor(integration, classLoader);
        configMybatisProcessor(integration, classLoader);
    }

    private void configMybatisPlusProcessor(Integration integration, ClassLoader classLoader) {
        //if there has MybatisPlus ClassResource
        if (!mp_mark.exists()) {
            log.infoEcho("Add CBP for mybatis-plus core classes...");
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.core.MybatisConfiguration", new MybatisConfigurationCBP());
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.core.MybatisMapperAnnotationBuilder", new MybatisMapperAnnotationBuilderCBP());
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean", new MybatisSqlSessionFactoryBeanCBP());
//        integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.core.override.MybatisMapperProxy", new MybatisMapperProxyCBP());
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory", new MybatisMapperProxyFactoryCBP());
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.core.MybatisConfiguration$StrictMap", new StrictMapCBP());
        } else {
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.MybatisConfiguration", new MybatisConfigurationCBP());
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.MybatisMapperAnnotationBuilder", new MybatisMapperAnnotationBuilderCBP());
            integration.addIntegrationProcessor(classLoader, "com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean", new MybatisSqlSessionFactoryBeanCBP());
            integration.addIntegrationProcessor(classLoader, "org.apache.ibatis.binding.MapperProxy", new MybatisMapperProxyCBP());
            log.infoEcho("new");
        }
    }

    private void configMybatisProcessor(Integration integration, ClassLoader classLoader) {
        integration.addIntegrationProcessor(classLoader, "org.apache.ibatis.builder.xml.XMLMapperBuilder", new XMLMapperBuilderCBP());
    }

    @Override
    public boolean checkDependencies(ClassLoader classLoader, ClassResourceSource classResourceSource) {
        if (classResourceSource.getClassResource("com.baomidou.mybatisplus.MybatisConfiguration") != null) {
            tryCreateThenClean(true, mp_mark);
            return true;
        }
        return classResourceSource.getClassResource("com.baomidou.mybatisplus.core.MybatisConfiguration") != null;
    }

    private void tryCreateThenClean(boolean clzExist, File markFile) {
        if (clzExist) {
            if (!markFile.exists()) {
                try {
                    markFile.createNewFile();
                } catch (IOException e) {
                    log.infoEcho("markFilePath:" + markFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        } else {
            if (markFile.exists()) {
                markFile.delete();
            }
        }
    }

    @Override
    public String getId() {
        return "mybatis_plus_plugin";
    }

    @Override
    public String getName() {
        return "MybatisPlus_plugin";
    }

    @Override
    public String getDescription() {
        return "<li>A hook plugin for Support MybatisPlus that reloads modified SQL maps.</li>";
    }

    @Override
    public String getAuthor() {
        return "suchu";
    }

    @Override
    public String getWebsite() {
        return "https://githuboy.online";
    }

    @Override
    public String getSupportedVersions() {
        return null;
    }

    @Override
    public String getTestedVersions() {
        return null;
    }
}
