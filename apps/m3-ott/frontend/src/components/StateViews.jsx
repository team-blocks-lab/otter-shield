export function LoadingState({ label = "불러오는 중" }) {
  return (
    <div className="state" role="status" aria-live="polite">
      <span className="state__bar" aria-hidden="true" />
      <p>{label}</p>
    </div>
  );
}
export function ErrorState({ message, onRetry }) {
  return (
    <div className="state" role="alert">
      <h2 className="state__title">문제가 생겼습니다</h2>
      <p>{message || "잠시 후 다시 시도해 주세요."}</p>
      {onRetry && <button type="button" className="btn btn--secondary" onClick={onRetry}>다시 시도</button>}
    </div>
  );
}
export function EmptyState({ title, children }) {
  return (
    <div className="state">
      <h2 className="state__title">{title}</h2>
      {children && <p>{children}</p>}
    </div>
  );
}
