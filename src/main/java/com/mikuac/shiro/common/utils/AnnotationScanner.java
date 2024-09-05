package com.mikuac.shiro.common.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>ScanUtils class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class AnnotationScanner implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 扫描注解类
     *
     * @param packageName 包名
     * @return 注解集合
     */
    public Set<Class<?>> scan(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    .concat(ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName))
                            .concat("/*.class"));
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
            MetadataReader metadataReader;
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    metadataReader = new CachingMetadataReaderFactory(resourceLoader).getMetadataReader(resource);
                    // 当类型为注解时添加到集合
                    if (metadataReader.getClassMetadata().isAnnotation()) {
                        classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
            return classes;
        } catch (Exception e) {
            log.error("An exception occurred during annotation scanning: {}", e.getMessage(), e);
        }
        return classes;
    }

}
