package com.kafka.lib.config;

import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaUtils;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.AvroTypeException;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class CompressionConfig extends KafkaAvroSerializer {

    @SneakyThrows
    public byte[] serialize(String topic, Headers headers, Object data) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            AvroSchema schema = new AvroSchema(AvroSchemaUtils.getSchema(data, useSchemaReflection, avroReflectionAllowNull, removeJavaProperties));
            SpecificDatumWriter writer = new SpecificDatumWriter(schema.rawSchema());
            DataFileWriter dataFileWriter = new DataFileWriter(writer);
            try {
                if (data == null) {
                    throw new AvroTypeException("Exception while serializing the data");
                }
                dataFileWriter.setCodec(CodecFactory.deflateCodec(9));
                dataFileWriter.create(schema.rawSchema(), outputStream);
                dataFileWriter.append(data);
            } catch (Throwable var10) {
                try {
                    dataFileWriter.close();
                } catch (Throwable var9) {
                    var10.addSuppressed(var9);
                }
                throw var10;
            }
            dataFileWriter.close();
        } catch (AvroTypeException | IOException var11) {
            log.error("Exception while serializing the data " + var11.getMessage());
        }
        log.info("Compressed payload data size " + outputStream.toByteArray().length);

        return outputStream.toByteArray();
    }


}
