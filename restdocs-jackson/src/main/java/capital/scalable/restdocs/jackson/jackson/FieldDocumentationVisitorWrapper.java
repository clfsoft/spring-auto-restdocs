/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package capital.scalable.restdocs.jackson.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * @author Florian Benz
 */
public class FieldDocumentationVisitorWrapper implements JsonFormatVisitorWrapper {
    private SerializerProvider provider;
    private final FieldDocumentationVisitorContext context;
    private final String path;
    private final InternalFieldInfo fieldInfo;

    FieldDocumentationVisitorWrapper(FieldDocumentationVisitorContext context, String path,
            InternalFieldInfo fieldInfo) {
        this(null, context, path, fieldInfo);
    }

    FieldDocumentationVisitorWrapper(SerializerProvider provider,
            FieldDocumentationVisitorContext context, String path, InternalFieldInfo fieldInfo) {
        this.provider = provider;
        this.context = context;
        this.path = path;
        this.fieldInfo = fieldInfo;
    }

    public static FieldDocumentationVisitorWrapper create() {
        return new FieldDocumentationVisitorWrapper(new FieldDocumentationVisitorContext(), "",
                null);
    }

    @Override
    public SerializerProvider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(SerializerProvider provider) {
        this.provider = provider;
    }

    @Override
    public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.OBJECT);
        return new FieldDocumentationObjectVisitor(provider, type.getRawClass(), context, path);
    }

    @Override
    public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.ARRAY);
        return new FieldDocumentationArrayVisitor(provider, context, path);
    }

    @Override
    public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.STRING);
        return new JsonStringFormatVisitor.Base();
    }

    @Override
    public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.NUMBER);
        return new JsonNumberFormatVisitor.Base();
    }

    @Override
    public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.NUMBER);
        return new JsonIntegerFormatVisitor.Base();
    }

    @Override
    public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.BOOLEAN);
        return new JsonBooleanFormatVisitor.Base();
    }

    @Override
    public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.NULL);
        return new JsonNullFormatVisitor.Base();
    }

    @Override
    public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.VARIES);
        return new JsonAnyFormatVisitor.Base();
    }

    @Override
    public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
        addFieldIfPresent(JsonFieldType.OBJECT);
        return new JsonMapFormatVisitor.Base(provider);
    }

    public FieldDocumentationVisitorContext getContext() {
        return context;
    }

    private void addFieldIfPresent(JsonFieldType fieldType) {
        if (fieldInfo != null) {
            context.addField(fieldInfo, fieldType);
        }
    }
}
