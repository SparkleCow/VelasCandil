import {
  CategoryEnum,
  FeatureEnum,
  MaterialEnum,
} from '../../../../shared/models/candle.models';

export type ScentFamily = 'CITRICO' | 'DULCE' | 'AMADERADO' | 'FLORAL' | 'HERBAL';

export interface ScentFamilyOption {
  value: ScentFamily;
  label: string;
  icon: string;
}

export const SCENT_FAMILIES: ScentFamilyOption[] = [
  { value: 'CITRICO', label: 'Cítrico', icon: 'wb_sunny' },
  { value: 'DULCE', label: 'Dulce', icon: 'bakery_dining' },
  { value: 'AMADERADO', label: 'Amaderado', icon: 'forest' },
  { value: 'FLORAL', label: 'Floral', icon: 'local_florist' },
  { value: 'HERBAL', label: 'Herbal', icon: 'spa' },
];

const SCENT_KEYWORDS: Record<ScentFamily, string[]> = {
  CITRICO: [
    'cítrico',
    'citrico',
    'naranja',
    'limón',
    'limon',
    'bergamota',
    'mandarina',
    'pomelo',
    'lima',
  ],
  DULCE: [
    'vainilla',
    'canela',
    'caramelo',
    'dulce',
    'chocolate',
    'cacao',
    'miel',
    'almendra',
    'galleta',
    'postre',
  ],
  AMADERADO: [
    'sándalo',
    'sandalo',
    'cedro',
    'madera',
    'vetiver',
    'vetivert',
    'ahumado',
    'roble',
    'pino',
    'ámbar',
    'ambar',
    'oud',
  ],
  FLORAL: [
    'jazmín',
    'jasmin',
    'rosa',
    'lavanda',
    'geranio',
    'floral',
    'peonía',
    'peonia',
    'lirio',
    'tuberosa',
  ],
  HERBAL: [
    'eucalipto',
    'menta',
    'manzanilla',
    'salvia',
    'romero',
    'tomillo',
    'té verde',
    'te verde',
    'hierba',
    'albahaca',
  ],
};

export type SortKey = 'popular' | 'price_asc' | 'price_desc' | 'newest';

export interface SortOption {
  value: SortKey;
  label: string;
}

export const SORT_OPTIONS: SortOption[] = [
  { value: 'popular', label: 'Más populares' },
  { value: 'price_asc', label: 'Precio: menor a mayor' },
  { value: 'price_desc', label: 'Precio: mayor a menor' },
  { value: 'newest', label: 'Novedades' },
];

export const PRICE_SLIDER_MAX = 200_000;

export function detectScentFamily(
  name: string,
  description: string,
): ScentFamily | null {
  const haystack = `${name} ${description}`.toLowerCase();

  for (const [family, keywords] of Object.entries(SCENT_KEYWORDS)) {
    if (keywords.some((keyword) => haystack.includes(keyword))) {
      return family as ScentFamily;
    }
  }

  return null;
}

export function detectScentNote(
  name: string,
  description: string,
): string | null {
  const haystack = `${name} ${description}`.toLowerCase();
  const all = Object.values(SCENT_KEYWORDS).flat();
  const match = all.find((keyword) => haystack.includes(keyword));

  return match ? match.charAt(0).toUpperCase() + match.slice(1) : null;
}

export interface ProductBadge {
  icon: string;
  label: string;
  tone: 'soy' | 'gold' | 'stock' | 'out';
}

export function buildProductBadges(
  materialEnums: MaterialEnum[],
  featureEnums: FeatureEnum[],
  stock: number,
): ProductBadge[] {
  const badges: ProductBadge[] = [];

  if (materialEnums.includes('SOY_WAX')) {
    badges.push({ icon: 'eco', label: '100% Cera de Soya', tone: 'soy' });
  }

  if (featureEnums.includes('AROMATHERAPY')) {
    badges.push({ icon: 'spa', label: 'Aromaterapia', tone: 'gold' });
  }

  if (featureEnums.includes('GIFTABLE')) {
    badges.push({ icon: 'redeem', label: 'Para regalo', tone: 'gold' });
  }

  if (featureEnums.includes('HANDMADE')) {
    badges.push({ icon: 'volunteer_activism', label: 'Hecha a mano', tone: 'soy' });
  }

  if (stock === 0) {
    badges.push({ icon: 'block', label: 'Agotado', tone: 'out' });
  } else if (stock <= 5) {
    badges.push({ icon: 'local_fire_department', label: 'Últimas unidades', tone: 'stock' });
  }

  return badges.slice(0, 3);
}
