import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';

interface StatCardProps {
  kicker: string;
  value: ReactNode;
  description: string;
  actionLabel?: string;
  actionTo?: string;
}

export function StatCard({ kicker, value, description, actionLabel, actionTo }: StatCardProps) {
  return (
    <article className="dashboard-card">
      <p className="section-kicker">{kicker}</p>
      <strong>{value}</strong>
      <span>{description}</span>
      {actionLabel && actionTo && (
        <Link className="text-button link-button" to={actionTo}>
          {actionLabel}
        </Link>
      )}
    </article>
  );
}
