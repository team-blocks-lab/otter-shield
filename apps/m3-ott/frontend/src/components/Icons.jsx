const p = { width: 20, height: 20, viewBox: "0 0 24 24", fill: "currentColor", "aria-hidden": true };
export const PlayIcon = (props) => <svg {...p} {...props}><path d="M7.5 4.7a1.3 1.3 0 0 1 2 .05l10 7.1a.2.2 0 0 1 0 .3l-10 7.1A1.3 1.3 0 0 1 7.5 19V4.7Z" /></svg>;
export const PauseIcon = (props) => <svg {...p} {...props}><rect x="6" y="5" width="4" height="14" rx="1" /><rect x="14" y="5" width="4" height="14" rx="1" /></svg>;
export const InfoIcon = (props) => <svg {...p} {...props} fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><circle cx="12" cy="12" r="9" /><path d="M12 11v5M12 8h.01" /></svg>;
export const VolumeIcon = (props) => <svg {...p} {...props}><path d="M4 9v6h4l5 4V5L8 9H4Z" /><path d="M16 8.5a5 5 0 0 1 0 7" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>;
export const MutedIcon = (props) => <svg {...p} {...props}><path d="M4 9v6h4l5 4V5L8 9H4Z" /><path d="m16 9 5 6m0-6-5 6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" /></svg>;
export const CaptionIcon = (props) => <svg {...p} {...props} fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><rect x="3" y="5" width="18" height="14" rx="3" /><path d="M8 11h2.5M13.5 11H16M8 15h5M15.5 15H16" /></svg>;
export const FullIcon = (props) => <svg {...p} {...props} fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 9V4h5M20 9V4h-5M4 15v5h5M20 15v5h-5" /></svg>;
export const ChevronIcon = ({ dir = "right", ...rest }) => <svg {...p} {...rest} fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" style={{ transform: dir === "left" ? "scaleX(-1)" : undefined }}><path d="m9 5 7 7-7 7" /></svg>;
