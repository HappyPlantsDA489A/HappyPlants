import vikeReact from "vike-react/config";
import type { Config } from "vike/types";

export default {
  extends: [vikeReact],
  title: "Happy Plants",
  ssr: true,
} satisfies Config;
