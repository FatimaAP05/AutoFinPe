import type { ReactNode } from 'react';

interface MetricStripItem {
  label: string;
  value: ReactNode;
  tone?: 'default' | 'positive' | 'warning';
}

interface MetricStripProps {
  items: MetricStripItem[];
}

export function MetricStrip({ items }: MetricStripProps) {
  return (
    <div className="metric-strip">
      {items.map((item) => (
        <div className={`metric-pill metric-pill-${item.tone ?? 'default'}`} key={item.label}>
          <span>{item.label}</span>
          <strong>{item.value}</strong>
        </div>
      ))}
    </div>
  );
}
