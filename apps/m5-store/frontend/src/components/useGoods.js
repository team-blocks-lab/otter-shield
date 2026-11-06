import { useCallback, useEffect, useState } from 'react'
import { api } from '../api'

export function useGoods() {
  const [state, setState] = useState({ goods: [], status: 'loading', error: null })

  const load = useCallback(() => {
    let alive = true
    setState((s) => ({ ...s, status: 'loading', error: null }))
    api
      .getGoods()
      .then((goods) => alive && setState({ goods, status: 'ready', error: null }))
      .catch((error) => alive && setState({ goods: [], status: 'error', error }))
    return () => {
      alive = false
    }
  }, [])

  useEffect(() => load(), [load])
  return { ...state, reload: load }
}
