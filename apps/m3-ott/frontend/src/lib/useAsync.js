import { useEffect, useState, useCallback } from "react";

// fn(signal) → { status: loading|error|success, data, error, reload }
export function useAsync(fn, deps) {
  const [state, setState] = useState({ status: "loading", data: null, error: null });
  const [tick, setTick] = useState(0);
  useEffect(() => {
    const ac = new AbortController();
    setState((s) => ({ ...s, status: "loading", error: null }));
    fn(ac.signal)
      .then((data) => setState({ status: "success", data, error: null }))
      .catch((error) => { if (error.name !== "AbortError") setState({ status: "error", data: null, error }); });
    return () => ac.abort();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, tick]);
  const reload = useCallback(() => setTick((t) => t + 1), []);
  return { ...state, reload };
}
