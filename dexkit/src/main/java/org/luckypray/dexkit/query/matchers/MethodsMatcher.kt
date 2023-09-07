@file:Suppress("MemberVisibilityCanBePrivate", "unused", "INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package org.luckypray.dexkit.query.matchers

import com.google.flatbuffers.FlatBufferBuilder
import org.luckypray.dexkit.InnerMethodsMatcher
import org.luckypray.dexkit.query.base.BaseQuery
import org.luckypray.dexkit.query.enums.MatchType
import org.luckypray.dexkit.query.matchers.base.IntRange

class MethodsMatcher : BaseQuery() {
    var methodsMatcher: List<MethodMatcher>? = null
        private set
    @set:JvmSynthetic
    var matchType: MatchType = MatchType.Contains
    var rangeMatcher: IntRange? = null
        private set

    var count: Int
        @JvmSynthetic
        @Deprecated("Property can only be written.", level = DeprecationLevel.ERROR)
        get() = throw NotImplementedError()
        @JvmSynthetic
        set(value) {
            count(value)
        }

    fun methods(methods: List<MethodMatcher>) = also {
        this.methodsMatcher = methods
    }

    fun matchType(matchType: MatchType) = also {
        this.matchType = matchType
    }

    fun count(count: Int) = also {
        this.rangeMatcher = IntRange(count)
    }

    fun count(range: IntRange) = also {
        this.rangeMatcher = range
    }

    fun count(range: kotlin.ranges.IntRange) = also {
        rangeMatcher = IntRange(range)
    }

    fun count(min: Int, max: Int) = also {
        this.rangeMatcher = IntRange(min, max)
    }

    fun add(method: MethodMatcher) = also {
        methodsMatcher = methodsMatcher ?: mutableListOf()
        if (methodsMatcher !is MutableList) {
            methodsMatcher = methodsMatcher!!.toMutableList()
        }
        (methodsMatcher as MutableList<MethodMatcher>).add(method)
    }

    fun add(methodName: String) = also {
        add(MethodMatcher().apply { name(methodName) })
    }

    // region DSL

    @kotlin.internal.InlineOnly
    inline fun MethodsMatcher.add(init: MethodMatcher.() -> Unit) = also {
        add(MethodMatcher().apply(init))
    }

    // endregion

    companion object {
        @JvmStatic
        fun create() = MethodsMatcher()
    }
    
    override fun innerBuild(fbb: FlatBufferBuilder): Int {
        val root = InnerMethodsMatcher.createMethodsMatcher(
            fbb,
            methodsMatcher?.let { fbb.createVectorOfTables(it.map { it.build(fbb) }.toIntArray()) } ?: 0,
            matchType.value,
            rangeMatcher?.build(fbb) ?: 0
        )
        fbb.finish(root)
        return root
    }
}