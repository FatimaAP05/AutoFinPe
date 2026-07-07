import type { ReactNode } from 'react';

interface PageHeaderProps {
  kicker: string;
  title: string;
  titleId?: string;
  action?: ReactNode;
}

export function PageHeader({ kicker, title, titleId, action }: PageHeaderProps) {
  return (
    <div className="page-heading page-heading-row">
      <div>
        <p className="section-kicker">{kicker}</p>
        <h2 id={titleId}>{title}</h2>
      </div>
      {action && <div className="page-heading-action">{action}</div>}
    </div>
  );
}
