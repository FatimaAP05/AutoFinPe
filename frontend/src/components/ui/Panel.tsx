import type { ReactNode } from 'react';

interface PanelProps {
  title?: string;
  className?: string;
  children: ReactNode;
}

export function Panel({ title, className = '', children }: PanelProps) {
  return (
    <section className={`ui-panel ${className}`.trim()}>
      {title && (
        <div className="panel-heading">
          <h3>{title}</h3>
        </div>
      )}
      {children}
    </section>
  );
}
