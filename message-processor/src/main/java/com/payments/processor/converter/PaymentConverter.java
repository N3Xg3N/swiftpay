package com.payments.processor.converter;

import java.util.List;

/**
 * Interface for converting between payment message formats.
 * Implementations handle specific source formats (e.g., pacs.009.001.08).
 *
 * @param <S> Source format type
 * @param <T> Target format type
 */
public interface PaymentConverter<S, T> {

    /**
     * Convert a single payment message from source to target format.
     *
     * @param source the source message
     * @return the converted target message
     * @throws ConversionException if conversion fails
     */
    T convert(S source) throws ConversionException;

    /**
     * Convert multiple payment messages in batch.
     *
     * @param sources list of source messages
     * @return list of converted target messages
     * @throws ConversionException if conversion fails
     */
    List<T> convertBatch(List<S> sources) throws ConversionException;

    /**
     * Validates the source message before attempting conversion.
     *
     * @param source the source message to validate
     * @return validation result with any errors/warnings
     */
    ConversionValidationResult validate(S source);
}
