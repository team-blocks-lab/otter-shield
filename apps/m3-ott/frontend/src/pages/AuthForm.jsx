import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Link, navigate } from "../lib/router";

// 백엔드 SignupRequest / LoginRequest 검증 규칙과 같다. 최종 검증은 서버가 하며 fieldErrors 를 그대로 표시한다.
const RULES = {
  name: (v) => (!v ? "이름을 입력해 주세요." : v.length > 50 ? "이름은 50자 이내로 입력해 주세요." : v !== v.trim() ? "이름 앞뒤에 공백을 넣을 수 없습니다." : ""),
  loginId: (v) => (/^[a-z0-9]{4,30}$/.test(v) ? "" : "아이디는 영문 소문자와 숫자 4~30자로 입력해 주세요."),
  password: (v) => (v.length >= 8 && v.length <= 64 ? "" : "비밀번호는 8~64자로 입력해 주세요."),
};
const LABEL = { name: "이름", loginId: "아이디", password: "비밀번호" };

export default function AuthForm({ mode }) {
  const isSignup = mode === "signup";
  const { login, signup } = useAuth();
  const [v, setV] = useState({ name: "", loginId: "", password: "" });
  const [errs, setErrs] = useState({});
  const [formErr, setFormErr] = useState("");
  const [busy, setBusy] = useState(false);
  const fields = isSignup ? ["name", "loginId", "password"] : ["loginId", "password"];
  const set = (k) => (e) => setV((s) => ({ ...s, [k]: e.target.value }));

  const submit = async (e) => {
    e.preventDefault();
    setFormErr("");
    const next = {};
    fields.forEach((k) => { const m = isSignup ? RULES[k](v[k]) : v[k] ? "" : `${LABEL[k]}을(를) 입력해 주세요.`; if (m) next[k] = m; });
    setErrs(next);
    if (Object.keys(next).length) { document.getElementById(`f-${Object.keys(next)[0]}`)?.focus(); return; }
    setBusy(true);
    try {
      if (isSignup) { await signup({ name: v.name, loginId: v.loginId, password: v.password }); navigate("/login?joined=1"); }
      else { await login({ loginId: v.loginId, password: v.password }); navigate("/"); }
    } catch (err) {
      if (err.fieldErrors && Object.keys(err.fieldErrors).length) setErrs(err.fieldErrors);
      setFormErr(err.message || "처리하지 못했습니다.");
    } finally { setBusy(false); }
  };
  const joined = !isSignup && window.location.hash.includes("joined=1");

  return (
    <div className="container auth page-pad">
      <div className="auth__panel">
        <h1 className="auth__title">{isSignup ? "회원가입" : "로그인"}</h1>
        {joined && <p className="notice" role="status">가입이 완료되었습니다. 로그인해 주세요.</p>}
        <form onSubmit={submit} noValidate className="form">
          {fields.map((k) => (
            <div className="field" key={k}>
              <label htmlFor={`f-${k}`}>{LABEL[k]}</label>
              <input id={`f-${k}`} name={k} type={k === "password" ? "password" : "text"} value={v[k]} onChange={set(k)}
                autoComplete={k === "password" ? (isSignup ? "new-password" : "current-password") : k === "loginId" ? "username" : "name"}
                aria-invalid={!!errs[k]} aria-describedby={errs[k] ? `e-${k}` : undefined} />
              {errs[k] && <p className="field__err" id={`e-${k}`}>{errs[k]}</p>}
            </div>
          ))}
          {formErr && <p className="field__err" role="alert">{formErr}</p>}
          <button type="submit" className="btn btn--primary btn--lg" disabled={busy}>{busy ? "처리 중" : isSignup ? "가입하기" : "로그인"}</button>
        </form>
        <p className="auth__switch">{isSignup ? <>이미 계정이 있나요? <Link to="/login">로그인</Link></> : <>계정이 없나요? <Link to="/signup">회원가입</Link></>}</p>
      </div>
    </div>
  );
}
