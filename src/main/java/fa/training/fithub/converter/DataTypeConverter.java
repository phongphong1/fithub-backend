//package fa.training.fithub.converter;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import fa.training.fithub.enums.DataType;
//
//@Converter(autoApply = true)
//public class DataTypeConverter implements AttributeConverter<DataType, String> {
//
//    @Override
//    public String convertToDatabaseColumn(DataType attribute) {
//        if (attribute == null) return null;
//        return attribute.name(); // Lưu vào DB chữ hoa
//    }
//
//    @Override
//    public DataType convertToEntityAttribute(String dbData) {
//        if (dbData == null) return null;
//        return DataType.valueOf(dbData.toUpperCase()); // Load từ DB không phân biệt hoa thường
//    }
//}
