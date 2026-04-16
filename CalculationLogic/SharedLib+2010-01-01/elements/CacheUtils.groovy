/**
 * Uses getOrSetMap method passing api.global as default map
 * @param key Dictionary key to this object in map
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @param invalidateCache Invalidate existing value from cache and populate the new value by running closure
 * @return Returns the result of the cache access, and mutates the cache map
 */
def getOrSet(String key, Closure closure, Boolean cacheNulls = false, Boolean invalidateCache = false) {
    return getOrSet(key, null, closure, cacheNulls, invalidateCache)
}

/**
 * Uses getOrSetMap method passing api.global as default map
 * @param key Dictionary key to this object in map
 * @param args Array of inputs to give to closure
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @param invalidateCache Invalidate existing value from cache and populate the new value by running closure
 * @return Returns the result of the cache access, and mutates the cache map
 */
def getOrSet(String key, List args, Closure closure, Boolean cacheNulls = false, Boolean invalidateCache = false) {
    return getOrSetMap(api.global, key, args, closure, cacheNulls, invalidateCache)
}

/**
 * Cache objects in provided map, run closure to fill
 * @param map The map to store objects in
 * @param key Dictionary key to this object in map
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @param invalidateCache Invalidate existing value from cache and populate the new value by running closure
 * @return Returns the result of the cache access, and mutates the cache map
 * EXAMPLE USAGE:
 * def lCache = [:]
 * def res = getOrSetMap(lCache, "SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetMap(Map map, String key, Closure closure, Boolean cacheNulls = false, Boolean invalidateCache = false) {
    return getOrSetMap(map, key, null, closure, cacheNulls, invalidateCache)
}

/**
 * Cache objects in provided map, run closure to fill
 * @param map The map to store objects in
 * @param key Dictionary key to this object in map
 * @param args Array of inputs to give to closure
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @param invalidateCache Invalidate existing value from cache and populate the new value by running closure
 * @return Returns the result of the cache access, and mutates the cache map
 * EXAMPLE USAGE:
 * def lCache = [:]
 * def res = getOrSetMap(lCache, "SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetMap(Map map, String key, List args, Closure closure, Boolean cacheNulls = false, Boolean invalidateCache = false) {
    def res = map[key]
    if (invalidateCache || !map.containsKey(key) || (!cacheNulls && res == null)) {
        res = closure(*args)
        if (res != null || cacheNulls) {
            map[key] = res
        }
    }
    return res
}

/**
 * Cache objects in shared cache, run closure to fill
 * @param key Dictionary key to this object in cache
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @param invalidateCache Invalidate existing value from cache and populate the new value by running closure
 * @return Returns the result of the cache access
 * EXAMPLE USAGE: def res = getOrSetShared("SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetShared(String key, Closure closure, Boolean cacheNulls = false, Boolean invalidateCache = false) {
    return getOrSetShared(key, null, closure, cacheNulls, invalidateCache)
}

/**
 * Cache objects in shared cache, run closure to fill
 * @param key Dictionary key to this object in cache
 * @param args Array of inputs to give to closure
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @param invalidateCache Invalidate existing value from cache and populate the new value by running closure
 * @return Returns the result of the cache access
 * EXAMPLE USAGE: def res = getOrSetShared("SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetShared(String key, List args, Closure closure, Boolean cacheNulls = false, Boolean invalidateCache = false) {
    Object res
    String cacheData

    if (invalidateCache) {
        api.removeSharedCache(key)
    } else {
        cacheData = api.getSharedCache(key)
        res = cacheData == "null" ? null : tryDecodeJSON(cacheData)
    }

    if (invalidateCache || (!cacheNulls && (res == null || res == "null")) || (cacheNulls && cacheData != "null" && res == null)) {
        res = closure(*args)
        if (res != null || cacheNulls) {
            api.setSharedCache(key, api.jsonEncode(res) ?: "null")
        }
    }
    return res
}

/**
 * Same as getOrSetShared, but with an enforced Max TTL before refresh is forced
 * Does not guarantee Minimum TTL, or removal from cache
 * Ironically uses cache to store TTL.
 * @param maxTTL Max time in Seconds before object must renew in cache - or be left for dead
 * @param key Shared cache key to this object
 * @param args Array of inputs to give to closure
 * @param closure Function to run to populate cache of nothing exists there
 * @return Returns the result of the cache access
 * EXAMPLE USAGE: def res = getOrSetShared(3600, "SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetShared(Integer maxTTL, String key, List args, Closure closure) {
    def ttlFromNow = org.joda.time.DateTime.now() + (maxTTL * 1000)//convert to MS
    def expiry = api.getSharedCache("TTL-${key}") // currently cached expiry time, if any

    // if expiry is set, check it and expire if past
    if (expiry && (expiry as long) >= ttlFromNow) {
        api.removeSharedCache(key)
    } else { // if expiry not set, set it
        api.setSharedCache("TTL-${key}", api.jsonEncode(ttlFromNow))
    }

    return getOrSet(key, args, closure)
}

/**
 * Cache objects in api.global and shared cache, run closure to fill
 * @param key Dictionary key to this object in cache
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @return Returns the result of the cache access
 * EXAMPLE USAGE: def res = getOrSetShared("SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetGlobalAndShared(String key, Closure closure, Boolean cacheNulls = false) {
    getOrSetGlobalAndShared(key, null, closure, cacheNulls)
}

/**
 * Cache objects in api.global and shared cache, run closure to fill
 * @param key Dictionary key to this object in cache
 * @param args Array of inputs to give to closure
 * @param closure Function to run to populate cache of nothing exists there
 * @param cacheNulls Allow null-values to be cached, default false results in re-execution of closure each call if null
 * @return Returns the result of the cache access
 * EXAMPLE USAGE: def res = getOrSetShared("SomeKey", [arg1, arg2, arg3], { arg1, arg2, arg3 -> DoSomething() })
 */
def getOrSetGlobalAndShared(String key, List args, Closure closure, Boolean cacheNulls = false) {
    def cached = api.global[key]
    // eager return from global, fallback to shared
    if (cached != null || (api.global.containsKey(key) && cacheNulls)) {
        return cached
    } else {
        cached = getOrSetShared(key, args, closure, cacheNulls)
        api.global[key] = cached
        return cached
    }
}

/**
 * An unfortunate hack I couldn't make more elegant...
 * Required by shared cache functions as everything must be stored as string.
 * Takes in a string of json and parses either a map, list, or number.
 * If first character not '[' or '{', or string input is not a number, function will just return what it was given.
 * @param str The string to attempt to parse from
 * @return List[], Map[:], BigDecimal, Double, Integer or Object result of attempted parse
 */
def tryDecodeJSON(str) {
    if (str instanceof String) {
        if (str.isNumber()) {
            if (str.isInteger()) {
                return str as Integer
            } else if (str.isBigDecimal()) {
                return str as BigDecimal
            } else if (str.isDouble()) {
                return str as Double
            }
        } else if (str.startsWith("[")) {
            return api.jsonDecodeList(str)
        } else if (str.startsWith("{")) {
            return api.jsonDecode(str)
        } else if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1)
        } else {
            return str
        }
    } else {
        return str
    }
}

