import { IngredientRequest } from './ingredient.models';

export type CategoryEnum =
  | 'AROMATIC'
  | 'DECORATIVE'
  | 'RELIGIOUS'
  | 'WAX_MELT'
  | 'WAX_WARMER';

export type MaterialEnum =
  | 'SOY_WAX'
  | 'BEESWAX'
  | 'PARAFFIN_WAX'
  | 'COCONUT_WAX'
  | 'PALM_WAX'
  | 'GEL_WAX';

export type FeatureEnum =
  | 'HANDMADE'
  | 'SCENTED'
  | 'UNSCENTED'
  | 'REUSABLE'
  | 'REFILLABLE'
  | 'AROMATHERAPY'
  | 'MEDITATION'
  | 'RELAXATION'
  | 'DECORATIVE'
  | 'GIFTABLE';

export interface SelectOption<T> {
  value: T;
  label: string;
}

export const CATEGORIES: SelectOption<CategoryEnum>[] = [
  { value: 'AROMATIC', label: 'Aromática' },
  { value: 'DECORATIVE', label: 'Decorativa' },
  { value: 'RELIGIOUS', label: 'Religiosa' },
  { value: 'WAX_MELT', label: 'Wax Melt' },
  { value: 'WAX_WARMER', label: 'Quemador de Cera' },
];

export const MATERIALS: SelectOption<MaterialEnum>[] = [
  { value: 'SOY_WAX', label: 'Cera de Soya' },
  { value: 'BEESWAX', label: 'Cera de Abejas' },
  { value: 'PARAFFIN_WAX', label: 'Parafina' },
  { value: 'COCONUT_WAX', label: 'Cera de Coco' },
  { value: 'PALM_WAX', label: 'Cera de Palma' },
  { value: 'GEL_WAX', label: 'Cera Gel' },
];

export const FEATURES: SelectOption<FeatureEnum>[] = [
  { value: 'HANDMADE', label: 'Hecha a Mano' },
  { value: 'SCENTED', label: 'Aromática' },
  { value: 'UNSCENTED', label: 'Sin Aroma' },
  { value: 'REUSABLE', label: 'Reutilizable' },
  { value: 'REFILLABLE', label: 'Recargable' },
  { value: 'AROMATHERAPY', label: 'Aromaterapia' },
  { value: 'MEDITATION', label: 'Meditación' },
  { value: 'RELAXATION', label: 'Relajación' },
  { value: 'DECORATIVE', label: 'Decorativa' },
  { value: 'GIFTABLE', label: 'Ideal para Regalo' },
];

export interface IngredientResponse {
  name: string;
  amount: number;
  price: number;
}

export interface CandleRequest {
  name: string;
  description: string;
  principalImage: string;
  stock: number;
  materialEnums: MaterialEnum[];
  featureEnums: FeatureEnum[];
  categories: CategoryEnum[];
  images: string[];
  ingredients: IngredientRequest[];
}

export interface CandleUpdateRequest {
  name: string;
  description: string;
  principalImage: string;
  stock: number;
  materialEnums: MaterialEnum[];
  featureEnums: FeatureEnum[];
  categories: CategoryEnum[];
  images: string[];
}

export interface CandleResponse {
  id: number;
  name: string;
  description: string;
  principalImage: string;
  stock: number;
  price: number;
  materialEnums: MaterialEnum[];
  featureEnums: FeatureEnum[];
  categories: CategoryEnum[];
  images: string[];
  ingredients: IngredientResponse[];
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
