@file:Suppress("MemberVisibilityCanBePrivate", "unused", "INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package org.luckypray.dexkit.query

import com.google.flatbuffers.FlatBufferBuilder
import org.luckypray.dexkit.InnerFindClass
import org.luckypray.dexkit.query.base.BaseQuery
import org.luckypray.dexkit.query.matchers.ClassMatcher
import org.luckypray.dexkit.result.ClassData

class FindClass : BaseQuery() {
    /**
     * Search classes in the specified packages.
     * ----------------
     * 在指定的包中搜索类。
     */
    @set:JvmSynthetic
    var searchPackages: List<String>? = null

    /**
     * Exclude classes in the specified packages.
     * ----------------
     * 排除指定包中的类。
     */
    @set:JvmSynthetic
    var excludePackages: List<String>? = null

    /**
     * Ignore case with [searchPackages] and [excludePackages].
     * ----------------
     * 忽略 [searchPackages] 和 [excludePackages] 的大小写。
     */
    @set:JvmSynthetic
    var ignorePackagesCase: Boolean = false

    /**
     * Searches the specified [ClassData] list for classes matching the criteria.
     * ----------------
     * 在指定的 [ClassData] 列表中搜索匹配符合条件的类。
     */
    @set:JvmSynthetic
    var searchClasses: List<ClassData>? = null

    /**
     * Terminates the search after finding the first matching class.
     * ----------------
     * 找到第一个匹配的类后终止搜索。
     */
    @set:JvmSynthetic
    var findFirst: Boolean = false

    var matcher: ClassMatcher? = null
        private set

    /**
     * Search classes in the specified packages.
     * ----------------
     * 在指定的包中搜索类。
     *
     *     searchPackages("java.lang", "java.util")
     *
     * @param searchPackages search packages / 搜索包
     * @return [FindClass]
     */
    fun searchPackages(vararg searchPackages: String) = also {
        this.searchPackages = searchPackages.toList()
    }

    /**
     * Search classes in the specified packages.
     * ----------------
     * 在指定的包中搜索类。
     *
     *     searchPackages(listOf("java.lang", "java.util"))
     *
     * @param searchPackages search packages / 搜索包
     * @return [FindClass]
     */
    fun searchPackages(searchPackages: List<String>) = also {
        this.searchPackages = searchPackages
    }

    /**
     * Exclude classes in the specified packages.
     * ----------------
     * 排除指定包中的类。
     *
     *     excludePackages("java.lang", "java.util")
     *
     * @param excludePackages exclude packages / 排除包
     * @return [FindClass]
     */
    fun excludePackages(vararg excludePackages: String) = also {
        this.excludePackages = excludePackages.toList()
    }

    /**
     * Exclude classes in the specified packages.
     * ----------------
     * 排除指定包中的类。
     *
     *     excludePackages(listOf("java.lang", "java.util"))
     *
     * @param excludePackages exclude packages / 排除包
     * @return [FindClass]
     */
    fun excludePackages(excludePackages: List<String>) = also {
        this.excludePackages = excludePackages
    }

    /**
     * Ignore case with [searchPackages] and [excludePackages].
     * ----------------
     * 忽略 [searchPackages] 和 [excludePackages] 的大小写。
     *
     * @param ignorePackagesCase ignore case / 忽略大小写
     * @return [FindClass]
     */
    fun ignorePackagesCase(ignorePackagesCase: Boolean) = also {
        this.ignorePackagesCase = ignorePackagesCase
    }

    /**
     * Search in the specified [ClassData] list.
     * ----------------
     * 在指定的 [ClassData] 列表中搜索指定类。
     *
     * @param classes search classes / 类列表
     * @return [FindClass]
     */
    fun searchInClass(classes: List<ClassData>) = also {
        this.searchClasses = classes
    }

    /**
     * Build a [ClassMatcher] to match classes.
     * ----------------
     * 构建一个 [ClassMatcher] 来匹配类。
     *
     *     matcher(ClassMatcher.create().superClass("android.app.Activity"))
     *
     * @param matcher class matcher / 类匹配器
     * @return [FindClass]
     */
    fun matcher(matcher: ClassMatcher) = also {
        this.matcher = matcher
    }

    // region DSL

    /**
     * @see matcher
     */
    @kotlin.internal.InlineOnly
    inline fun matcher(init: ClassMatcher.() -> Unit) = also {
        matcher(ClassMatcher().apply(init))
    }

    // endregion

    companion object {
        @JvmStatic
        fun create() = FindClass()
    }

    override fun innerBuild(fbb: FlatBufferBuilder): Int {
        val root = InnerFindClass.createFindClass(
            fbb,
            searchPackages?.map { fbb.createString(it) }?.toIntArray()
                ?.let { fbb.createVectorOfTables(it) } ?: 0,
            excludePackages?.map { fbb.createString(it) }?.toIntArray()
                ?.let { fbb.createVectorOfTables(it) } ?: 0,
            ignorePackagesCase,
            searchClasses?.map { getEncodeId(it.dexId, it.id) }?.toLongArray()
                ?.let { InnerFindClass.createInClassesVector(fbb, it) } ?: 0,
            findFirst,
            matcher?.build(fbb) ?: 0
        )
        fbb.finish(root)
        return root
    }
}