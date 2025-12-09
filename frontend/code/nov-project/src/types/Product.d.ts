export interface Product {
  id: number;
  parentSku: string | null;
  parentId: number | null;
  type: string;
  sku: string;
  name: string;
  shortDescription: string | null;
  description: string;
  taxStatus: string;
  taxClass: string;
  inStock: boolean;
  stockQuantity: number | null;
  weightKg: number | null;
  lengthCm: number | null;
  widthCm: number | null;
  heightCm: number | null;
  shippingClass: string | null;
  brands: string;
  images: string | null; // "url1, url2, ..."

  price1: number;
  price2: number;
  price3: number;
  price4: number;

  // if visible is true then the value of attribute can be selected
  attribute1Name: string | null;
  attribute1Values: string | null;
  attribute1Visible: boolean | null;
  attribute1Global: boolean | null;
  attribute1Default: string | null;
  attribute2Name: string | null;
  attribute2Values: string | null;
  attribute2Visible: boolean | null;
  attribute2Global: boolean | null;
  attribute3Name: string | null;
  attribute3Values: string | null;
  attribute3Visible: boolean | null;
  attribute3Global: boolean | null;
}

export interface ProductListResponse {
  content: Product[];
  pageable: Pageable;
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: Sort;
  numberOfElements: number; // default = 10
  first: boolean;
  empty: boolean;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
  offset: number;
  paged: boolean;
  unpaged: boolean;
  sort: Sort;
}

export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface Category {
  id: number;
  name: string;
  parentId: number | null;
  children: Category[];
}
