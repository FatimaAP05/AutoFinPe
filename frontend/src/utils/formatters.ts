export function formatCurrency(value: number, currency: 'PEN' | 'USD'): string {
  return new Intl.NumberFormat('es-PE', {
    style: 'currency',
    currency,
  }).format(value);
}
