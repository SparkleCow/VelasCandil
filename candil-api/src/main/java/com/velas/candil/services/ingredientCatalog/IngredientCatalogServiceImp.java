package com.velas.candil.services.ingredientCatalog;

import com.velas.candil.entities.ingredient.IngredientCatalog;
import com.velas.candil.models.ingredient.IngredientType;
import com.velas.candil.models.ingredient.IngredientUnit;
import com.velas.candil.repositories.IngredientCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class IngredientCatalogServiceImp implements IngredientCatalogService {

    private final IngredientCatalogRepository ingredientCatalogRepository;

    @Override
    public void importExcel(MultipartFile file) {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null || row.getCell(0) == null) {
                    continue;
                }

                IngredientCatalog ingredientCatalog = mapRow(row);

                ingredientCatalogRepository
                        .findByIngredientCode(ingredientCatalog.getIngredientCode())
                        .ifPresentOrElse(existing -> {

                            existing.setIngredientName(ingredientCatalog.getIngredientName());
                            existing.setType(ingredientCatalog.getType());
                            existing.setIngredientUnit(ingredientCatalog.getIngredientUnit());
                            existing.setPricePerUnit(ingredientCatalog.getPricePerUnit());
                            existing.setSupplier(ingredientCatalog.getSupplier());
                            existing.setSupplierContact(ingredientCatalog.getSupplierContact());
                            existing.setActive(true);

                            ingredientCatalogRepository.save(existing);

                        }, () -> ingredientCatalogRepository.save(ingredientCatalog));
            }

        } catch (IOException e) {
            throw new RuntimeException("Error importing excel file", e);
        }
    }

    @Override
    public List<IngredientCatalog> findAllIngredients() {
        return ingredientCatalogRepository.findAll();
    }

    private IngredientCatalog mapRow(Row row) {

        String code = row.getCell(0).getStringCellValue().trim();
        String name = row.getCell(1).getStringCellValue().trim();
        String type = row.getCell(2).getStringCellValue().trim();
        String unit = row.getCell(3).getStringCellValue().trim();

        BigDecimal price = BigDecimal.valueOf(
                row.getCell(4).getNumericCellValue()
        );

        String supplier = row.getCell(5).getStringCellValue().trim();
        String contact = row.getCell(6).getStringCellValue().trim();

        return IngredientCatalog.builder()
                .ingredientCode(code)
                .ingredientName(name)
                .type(IngredientType.valueOf(type.toUpperCase()))
                .ingredientUnit(IngredientUnit.fromSymbol(unit))
                .pricePerUnit(price)
                .supplier(supplier)
                .supplierContact(contact)
                .active(true)
                .build();
    }
}
