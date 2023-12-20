package org.dromara.common.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import cn.hutool.core.util.StrUtil;
import org.dromara.common.core.utils.reflect.ReflectUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.dromara.common.core.utils.StringUtils.SEPARATOR;

/**
 * 扩展 hutool TreeUtil 封装系统树构建
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeBuildUtils extends TreeUtil {

    /**
     * 根据前端定制差异化字段
     */
    public static final TreeNodeConfig DEFAULT_CONFIG = TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label");

    public static <T, K> List<Tree<K>> build(List<T> list, NodeParser<T, K> nodeParser) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }

        K k = ReflectUtils.invokeGetter(list.get(0), "parentId");
        return TreeUtil.build(list, k, DEFAULT_CONFIG, nodeParser);
    }

    public static <T, K> List<Tree<K>> buildDept(List<T> list, NodeParser<T, K> nodeParser) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }

        AtomicReference<K> kAtomic = new AtomicReference<>();
        String ancestors = ReflectUtils.invokeGetter(list.get(0), "ancestors");
        if (StrUtil.isNotBlank(ancestors)) {
            String[] split = ancestors.split(SEPARATOR);
            if (split.length > 0) {
                for (String s : split) {
                    boolean flag = list.stream().anyMatch(item -> {
                        K parentId = ReflectUtils.invokeGetter(item, "parentId");
                        if (s.equals(parentId.toString())) {
                            kAtomic.set(parentId);
                            return true;
                        }
                        return false;
                    });
                    if (flag) {
                        break;
                    }
                }
            }
        }
        if (kAtomic.get() == null) {
            kAtomic.set(ReflectUtils.invokeGetter(list.get(0), "parentId"));
        }
        return TreeUtil.build(list, kAtomic.get(), DEFAULT_CONFIG, nodeParser);
    }

}
