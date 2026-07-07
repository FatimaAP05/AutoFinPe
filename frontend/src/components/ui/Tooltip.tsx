import type { ReactNode } from 'react';

interface TooltipProps {
  children: ReactNode;
}

export function Tooltip({ children }: TooltipProps) {
  return (
    <span className="tooltip-wrapper">
      <span className="tooltip-trigger" tabIndex={0} aria-label="Ayuda contextual">
        ?
      </span>
      <span className="tooltip-content" role="tooltip">
        {children}
      </span>
    </span>
  );
}
