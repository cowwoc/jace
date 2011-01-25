BEGIN_NAMESPACE_2(org, jace)

/**
 * Contains the template specialization prototypes for the template class, ElementProxy.
 *
 * This file is only for internal use of the JACE library.
 */
template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JBoolean >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JBoolean >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JBoolean& ElementProxy< ::org::jace::proxy::types::JBoolean >::operator=(const ::org::jace::proxy::types::JBoolean& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JByte >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JByte >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JByte& ElementProxy< ::org::jace::proxy::types::JByte >::operator=(const ::org::jace::proxy::types::JByte& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JChar >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JChar >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JChar& ElementProxy< ::org::jace::proxy::types::JChar >::operator=(const ::org::jace::proxy::types::JChar& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JDouble >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JDouble >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JDouble& ElementProxy< ::org::jace::proxy::types::JDouble >::operator=(const ::org::jace::proxy::types::JDouble& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JFloat >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JFloat >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JFloat& ElementProxy< ::org::jace::proxy::types::JFloat >::operator=(const ::org::jace::proxy::types::JFloat& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JInt >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JInt >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JInt& ElementProxy< ::org::jace::proxy::types::JInt >::operator=(const ::org::jace::proxy::types::JInt& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JLong >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JLong >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JLong& ElementProxy< ::org::jace::proxy::types::JLong >::operator=(const ::org::jace::proxy::types::JLong& type);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JShort >::ElementProxy(jarray array, jvalue element, int index);

template <>
JACE_API ElementProxy< ::org::jace::proxy::types::JShort >::ElementProxy(const ElementProxy& proxy);

template <>
JACE_API ::org::jace::proxy::types::JShort& ElementProxy< ::org::jace::proxy::types::JShort >::operator=(const ::org::jace::proxy::types::JShort& type);

END_NAMESPACE_2(org, jace)
