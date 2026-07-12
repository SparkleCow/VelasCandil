export type IngredientType =
  | 'WAX'
  | 'FRAGRANCE'
  | 'COLORANT'
  | 'ADDITIVES'
  | 'WICK'
  | 'CONTAINER'
  | 'PACKAGING';

export const INGREDIENT_TYPES: IngredientType[] = [
  'WAX',
  'FRAGRANCE',
  'COLORANT',
  'ADDITIVES',
  'WICK',
  'CONTAINER',
  'PACKAGING',
];

export type IngredientsEnum =
  | 'BEESWAX'
  | 'SOY_WAX'
  | 'PARAFFIN_WAX'
  | 'COCONUT_WAX'
  | 'RAPSOY_WAX'
  | 'STEARIC_ACID'
  | 'VYBAR'
  | 'ESSENTIAL_OIL'
  | 'FRAGRANCE_OIL'
  | 'CANDLE_DYE'
  | 'MICA_POWDER'
  | 'WICK_COTTON'
  | 'WICK_WOOD'
  | 'WICK_TAB'
  | 'GLASS_JAR_SMALL'
  | 'GLASS_JAR_MEDIUM'
  | 'GLASS_JAR_LARGE'
  | 'METAL_TIN'
  | 'LID_METAL'
  | 'LABEL'
  | 'BRANDED_STICKER'
  | 'PACKAGING_BOX'
  | 'FILLER_PAPER'
  | 'PACKING_TAPE';

export const INGREDIENTS_ENUM_LIST: IngredientsEnum[] = [
  'BEESWAX',
  'SOY_WAX',
  'PARAFFIN_WAX',
  'COCONUT_WAX',
  'RAPSOY_WAX',
  'STEARIC_ACID',
  'VYBAR',
  'ESSENTIAL_OIL',
  'FRAGRANCE_OIL',
  'CANDLE_DYE',
  'MICA_POWDER',
  'WICK_COTTON',
  'WICK_WOOD',
  'WICK_TAB',
  'GLASS_JAR_SMALL',
  'GLASS_JAR_MEDIUM',
  'GLASS_JAR_LARGE',
  'METAL_TIN',
  'LID_METAL',
  'LABEL',
  'BRANDED_STICKER',
  'PACKAGING_BOX',
  'FILLER_PAPER',
  'PACKING_TAPE',
];

export interface IngredientUpdateRequest {
  name: IngredientsEnum;
  amount: number;
}

export interface IngredientRequest {
  ingredientId: number;
  amount: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface IngredientCatalogResponse {
  id: number;
  ingredientCode: string;
  ingredientName: string;
  type: string;
  ingredientUnit: string;
  pricePerUnit: number;
  supplier: string;
  supplierContact: string;
  active: boolean;
}

export interface IngredientResponse {
  name: IngredientsEnum;
  amount: number;
  price: number;
}
