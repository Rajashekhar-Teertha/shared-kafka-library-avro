package com.kafka.lib.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Slf4j
public class DecompressionConfig extends KafkaAvroDeserializer {


    /**
     * @param topic
     * @param headers
     * @param data
     * @return
     */
    @Override
    public Object deserialize(String topic, Headers headers, byte[] data) {
//        return super.deserialize(topic, headers, data);
        DatumReader userDatumReader = new SpecificDatumReader();
        DataFileReader dataFileReader = null;
        try {
            dataFileReader = new DataFileReader(new SeekableByteArrayInput(data),userDatumReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Object originalPayload = null;
         while (dataFileReader.hasNext()) {
             try {
                 originalPayload = dataFileReader.next(originalPayload);
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
        log.info("DeCompressed payload data size " + originalPayload.toString().length());

        return originalPayload;
    }
}
