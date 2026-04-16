import com.googlecode.genericdao.search.Filter

/**
 * Closable wrapper of api.stream(String typeCode, String sortBy, Filter... filters)
 * @param : typeCode
 * @param : sortBy
 * @param : filters. List of filters
 * @param : streamProcessor. Closure to process stream result. Closure should accept stream as parameter.
 *   Attention! Passing an empty closure { } can cause decreased performance and result in not closing streams which
 *   will leave hanging unclosed connections that can fill up connection pool and crash the server.
 * @return : result returned from streamProcessing closure
 * */
Object stream(String typeCode, String sortBy, List<Filter> filters, Closure streamProcessor = null) {
    return closeStream(api.stream(typeCode, sortBy, *filters), streamProcessor)
}

/**
 * Closable wrapper of api.stream(String typeCode, String sortBy, List<String> fields, Filter... filters)
 * @param : typeCode
 * @param : sortBy
 * @param : fields. List of fields
 * @param : filters. List of filters
 * @param : streamProcessor. Closure to process stream result. Closure should accept stream as parameter.
 *   Attention! Passing an empty closure { } can cause decreased performance and result in not closing streams which
 *   will leave hanging unclosed connections that can fill up connection pool and crash the server.
 * @return : result returned from streamProcessing closure
 * */
Object stream(String typeCode, String sortBy, List<String> fields, List<Filter> filters, Closure streamProcessor = null) {
    return closeStream(api.stream(typeCode, sortBy, fields, *filters), streamProcessor)
}

/**
 * Closable wrapper of api.stream(String typeCode, String sortBy, List<String> fields, boolean distinctValuesOnly, Filter... filters)
 * @param : typeCode
 * @param : sortBy
 * @param : fields. List of fields
 * @param : distinctValuesOnly
 * @param : filters. List of filters
 * @param : streamProcessor. Closure to process stream result. Closure should accept stream as parameter.
 *   Attention! Passing an empty closure { } can cause decreased performance and result in not closing streams which
 *   will leave hanging unclosed connections that can fill up connection pool and crash the server.
 * @return : result returned from streamProcessing closure
 * */
Object stream(String typeCode, String sortBy, List<String> fields, boolean distinctValuesOnly, List<Filter> filters, Closure streamProcessor = null) {
    return closeStream(api.stream(typeCode, sortBy, fields, distinctValuesOnly, *filters), streamProcessor)
}

/**
 * Self closable wrapper of api.stream(String typeCode, String sortBy, Map<String,String> fields, boolean distinctValuesOnly, Filter... filters)
 * @param : typeCode
 * @param : sortBy
 * @param : fields. Map of field
 * @param : distinctValuesOnly
 * @param : filters. List of filters
 * @param : streamProcessor. Closure to process stream result. Closure should accept stream as parameter.
 *   Attention! Passing an empty closure { } can cause decreased performance and result in not closing streams which
 *   will leave hanging unclosed connections that can fill up connection pool and crash the server.
 * @return : result returned from streamProcessing closure
 * */
Object stream(String typeCode, String sortBy, Map<String, String> fields, boolean distinctValuesOnly, List<Filter> filters, Closure streamProcessor = null) {
    return closeStream(api.stream(typeCode, sortBy, fields, distinctValuesOnly, *filters), streamProcessor)
}

/**
 * Utility to close stream after processing
 * @param stream
 * @param streamProcessor : Closure accept stream as parameter. If null or empty closure will be passed, then default
 *   collect operation will be used.
 * @return value returned by streamProcessor
 * @exception any exception during streamProcessor call will be thrown using api.throwException
 * */
Object closeStream(stream, Closure streamProcessor) {
    streamProcessor = streamProcessor ?: { it?.collect() }

    def result
    try {
        result = streamProcessor.call(stream)
    } catch (e) {
        api.throwException(e?.getMessage())
    } finally {
        stream?.close()
    }

    return result
}
