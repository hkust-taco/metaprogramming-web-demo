package mlscript.codegen

import mlscript.utils._
import mlscript.utils.shorthands._

// * Pretty printer for quasiquotes.
// * This is a temporary hack due to lack of runtime support and should be removed later.
object QQHelper {
  val prettyPrinter: Str = """
  |(() => {
  | const symbols = new Map();
  | const printList = (lst, sep) => {
  |   if (lst.length === 0) return "";
  |   else {
  |     const r = lst.reduce((x, y) => `${x}${y}${sep}`, "")
  |     return r.substring(0, r.length - sep.length);
  |   }
  | }
  | const indent = (s) => s.split("\n").map(ln => `  ${ln}`).join("\n")
  | globalThis.freshName = (n) => {
  |   if (!symbols.has(n)) { symbols.set(n, 0); }
  |   const i = symbols.get(n);
  |   symbols.set(n, i + 1);
  |   return `${n}_${i}`;
  | }
  | globalThis.Const = (n) => `${n}`;
  | globalThis.IntLit = (v) => `${v}`;
  | globalThis.DecLit = (v) => `${v}`;
  | globalThis.StrLit = (v) => `${v}`;
  | globalThis.UnitLit = (v) => `${v}`;
  | globalThis.Lam = (x, e) => `${x} =>\n${indent(e)}`;
  | globalThis.Var = (x) => `${x}`;
  | globalThis.App = (f, ...xs) => {
  | if (f === '+' || f === '-' || f === '*' || f === '/' || f === '==' || f === '<' || f === '>' || f === 'and' || f === 'or' || f === 'is')
  |   return `(${printList(xs, ` ${f} `)})`;
  | else
  |   return `${f}${printList(xs, ", ")}`;
  | }
  | globalThis.Rcd = (...xs) => `{${printList(xs, ", ")}}`;
  | globalThis.Bra = (x) => `(${x})`;
  | globalThis.Sel = (x, y) => `${x}.${y}`;
  | globalThis.Blk = (...s) => `{\n${indent(printList(s, ";\n"))}\n}`;
  | globalThis.Tup = (...es) => `(${printList(es, ", ")})`;
  | globalThis.Fld = (v) => `${v}`;
  | globalThis.Let = (nme, v, bod) => `let ${nme} =\n${indent(v)}\n${indent(`in ${bod}`)}`;
  | globalThis.Subs = (arr, idx) => `${arr}[${idx}]`;
  | globalThis.With = (lhs, rhs) => `${lsh} with ${rhs}`;
  | globalThis.Quoted = (body) => `code"${body}"`;
  | globalThis.CaseOf = (trm, cse) => `match ${trm}:\n  ${cse}`;
  | globalThis.Case = (pat, bod, trm) => `case ${pat} => ${bod}\n  ${trm})`;
  | globalThis.Wildcard = (res) => `_ => ${res}`;
  | globalThis.NoCases = () => `<NoCases>`;
  | globalThis.run = (code) => {console.log("Quoted:\n" + code);}
  |})();
  """.stripMargin

  val runtime: Str = """
  |(() => {
  |  const printList = (lst, sep) => {
  |    if (lst.length === 0) return "";
  |    else {
  |      const r = lst.reduce((x, y) => `${x.toString()}${y.toString()}${sep}`, "")
  |      return r.substring(0, r.length - sep.length);
  |    }
  |  }
  |  const indent = (s) => s.toString().split("\n").map(ln => `  ${ln}`).join("\n")
  |
  |  const symbols = new Map();
  |  globalThis.freshName = (n) => {
  |    if (!symbols.has(n)) { symbols.set(n, 0); }
  |    const i = symbols.get(n);
  |    symbols.set(n, i + 1);
  |    return `${n}_${i}`;
  |  }
  |
  |  class Code {};
  |  globalThis.try2String = function try2String(x) {
  |    if (x instanceof Code) return x.toString();
  |    else return x;
  |  }
  |
  |  class IntLit extends Code {
  |    #v
  |    constructor(v) {
  |      super();
  |      this.#v = v;
  |    }
  |    get v() {
  |      return this.#v;
  |    }
  |  }
  |  IntLit.prototype.toString = function toString() {
  |    return `${this.v}`;
  |  }
  |  globalThis.IntLit = (v) => Object.freeze(new IntLit(v));
  |  globalThis.IntLit.class = IntLit;
  |
  |  class DecLit extends Code {
  |    #v
  |    constructor(v) {
  |      super();
  |      this.#v = v;
  |    }
  |    get v() {
  |      return this.#v;
  |    }
  |  }
  |  DecLit.prototype.toString = function toString() {
  |    return `${this.v}`;
  |  }
  |  globalThis.DecLit = (v) => Object.freeze(new DecLit(v));
  |  globalThis.DecLit.class = DecLit;
  |
  |  class StrLit extends Code {
  |    #v
  |    constructor(v) {
  |      super();
  |      this.#v = v;
  |    }
  |    get v() {
  |      return this.#v;
  |    }
  |  }
  |  StrLit.prototype.toString = function toString() {
  |    return `"${this.v}"`;
  |  }
  |  globalThis.StrLit = (v) => Object.freeze(new StrLit(v));
  |  globalThis.StrLit.class = StrLit;
  |
  |  class UnitLit extends Code {
  |    #v
  |    constructor(v) {
  |      super();
  |      this.#v = v;
  |    }
  |    get v() {
  |      return this.#v;
  |    }
  |  }
  |  UnitLit.prototype.toString = function toString() {
  |    return this.v ? "undefined" : "null";
  |  }
  |  globalThis.UnitLit = (v) => Object.freeze(new UnitLit(v));
  |  globalThis.UnitLit.class = UnitLit;
  |
  |  class Lam extends Code {
  |    #x
  |    #e
  |    constructor(x, e) {
  |      super();
  |      this.#x = x;
  |      this.#e = e;
  |    }
  |    get x() {
  |      return this.#x;
  |    }
  |    get e() {
  |      return this.#e;
  |    }
  |  }
  |  Lam.prototype.toString = function toString() {
  |    return `${this.x} =>\n${indent(this.e)}`;
  |  }
  |  globalThis.Lam = (x, e) => Object.freeze(new Lam(x, e));
  |  globalThis.Lam.class = Lam;
  |
  |  class Var extends Code {
  |    #x
  |    constructor(x) {
  |      super();
  |      this.#x = x;
  |    }
  |    get x() {
  |      return this.#x;
  |    }
  |  }
  |  Var.prototype.toString = function toString() {
  |    return this.x;
  |  }
  |  globalThis.Var = (x) => Object.freeze(new Var(x));
  |  globalThis.Var.class = Var;
  |
  |  class App extends Code {
  |    #f
  |    #xs
  |    constructor(f, xs) {
  |      super();
  |      this.#f = f;
  |      this.#xs = xs;
  |    }
  |    get f() {
  |      return this.#f;
  |    }
  |    get xs() {
  |      return this.#xs;
  |    }
  |  }
  |  App.prototype.toString = function toString() {
  |    if (this.f instanceof Var) {
  |      const f = this.f.x;
  |      const xs = this.xs;
  |      if (f === '+' || f === '-' || f === '*' || f === '/' || f === '==' || f === '<' || f === '>' || f === 'and' || f === 'or' || f === 'is')
  |        return `(${printList(xs, ` ${f} `)})`;
  |      else
  |        return `${f}${printList(xs, ", ")}`;
  |      }
  |    else {
  |      return `${f}${printList(xs, ", ")}`;
  |    }
  |  }
  |  globalThis.App = (f, ...xs) => Object.freeze(new App(f, xs));;
  |  globalThis.App.class = App;
  |
  |  class Bra extends Code {
  |    #x
  |    constructor(x) {
  |      super();
  |      this.#x = x;
  |    }
  |    get x() {
  |      return this.#x;
  |    }
  |  }
  |  Bra.prototype.toString = function toString() {
  |    return `(${this.x})`;
  |  }
  |  globalThis.Bra = (x) => Object.freeze(new Bra(x));
  |  globalThis.Bra.class = Bra;
  |
  |  class Tup extends Code {
  |    #es
  |    constructor(es) {
  |      super();
  |      this.#es = es;
  |    }
  |    get es() {
  |      return this.#es;
  |    }
  |  }
  |  Tup.prototype.toString = function toString() {
  |    return `(${printList(this.es, ", ")})`;
  |  }
  |  globalThis.Tup = (...es) => Object.freeze(new Tup(es));
  |  globalThis.Tup.class = Tup;
  |
  |  class Fld extends Code {
  |    #v
  |    constructor(v) {
  |      super();
  |      this.#v = v;
  |    }
  |    get v() {
  |      return this.#v;
  |    }
  |  }
  |  Fld.prototype.toString = function toString() {
  |    return this.v;
  |  }
  |  globalThis.Fld = (v) => Object.freeze(new Fld(v));
  |  globalThis.Fld.class = Fld;
  |
  |  class Let extends Code {
  |    #nme
  |    #v
  |    #bod
  |    constructor(nme, v, bod) {
  |      super();
  |      this.#nme = nme;
  |      this.#v = v;
  |      this.#bod = bod;
  |    }
  |    get nme() {
  |      return this.#nme;
  |    }
  |    get v() {
  |      return this.#v;
  |    }
  |    get bod() {
  |      return this.#bod;
  |    }
  |  }
  |  Let.prototype.toString = function toString() {
  |    return `let ${this.nme} =\n${indent(this.v)}\n${indent(`in ${this.bod}`)}`;
  |  }
  |  globalThis.Let = (nme, v, bod) => Object.freeze(new Let(nme, v, bod));
  |  globalThis.Let.class = Let;
  |
  |  class CaseOf extends Code {
  |    #trm
  |    #cse
  |    constructor(trm, cse) {
  |      super();
  |      this.#trm = trm;
  |      this.#cse = cse;
  |    }
  |    get trm() {
  |      return this.#trm;
  |    }
  |    get cse() {
  |      return this.#cse;
  |    }
  |  }
  |  CaseOf.prototype.toString = function toString() {
  |    return `match ${this.trm}:\n  ${this.cse}`;
  |  }
  |  globalThis.CaseOf = (trm, cse) => Object.freeze(new CaseOf(trm, cse));
  |  globalThis.CaseOf.class = CaseOf;
  |
  |  class Case extends Code {
  |    #pat
  |    #bod
  |    #trm
  |    constructor(pat, bod, trm) {
  |      super();
  |      this.#pat = pat;
  |      this.#bod = bod;
  |      this.#trm = trm
  |    }
  |    get pat() {
  |      return this.#pat;
  |    }
  |    get bod() {
  |      return this.#bod;
  |    }
  |    get trm() {
  |      return this.#trm;
  |    }
  |  }
  |  Case.prototype.toString = function toString() {
  |    return `case ${this.pat} => ${this.bod}\n  ${this.trm})`;
  |  }
  |  globalThis.Case = (pat, bod, trm) => Object.freeze(new Case(pat, bod, trm));
  |  globalThis.Case.class = Case;
  |
  |  class Wildcard extends Code {
  |    #res
  |    constructor(res) {
  |      super();
  |      this.#res = res;
  |    }
  |    get res() {
  |      return this.#res;
  |    }
  |  }
  |  Wildcard.prototype.toString = function toString() {
  |    return `_ => ${this.res}`;
  |  }
  |  globalThis.Wildcard = (res) => Object.freeze(new Wildcard(res));
  |  globalThis.Wildcard.class = Wildcard;
  |
  |  class NoCases extends Code {
  |  }
  |  NoCases.prototype.toString = () => `<NoCases>`;
  |  globalThis.NoCases = () => Object.freeze(new NoCases());
  |  globalThis.NoCases.class = NoCases;
  |
  |  function isOp(f) {
  |    if (f instanceof Var) {
  |      const x = f.x;
  |      return x === '+' || x === '-' || x === '*' || x === '/' || x === '==' || x === '<' || x === '>';
  |    }
  |    return false;
  |  }
  |
  |  function runCase(v, cs, env) {
  |    if (cs instanceof Case) {
  |      const p = runRec(cs.pat, env);
  |      if (v === p) {
  |        return runRec(cs.bod, env);
  |      }
  |      else {
  |        return runCase(v, cs.trm, env);
  |      }
  |    }
  |    else if (cs instanceof Wildcard) {
  |      return runRec(cs.res, env);
  |    }
  |    else {
  |      throw new Error(`non-exhaustive case expression`);
  |    }
  |  }
  |
  |  function runRec(code, env) {
  |    if (code instanceof IntLit) {
  |      return code.v;
  |    }
  |    else if (code instanceof DecLit) {
  |      return code.v;
  |    }
  |    else if (code instanceof StrLit) {
  |      return code.v;
  |    }
  |    else if (code instanceof UnitLit) {
  |      return code.v ? undefined : null;
  |    }
  |    else if (code instanceof Lam) {
  |      const closEnv = new Map(env);
  |      return (...x) => {
  |        if (x.length !== code.x.es.length) {
  |          throw new Error(`the number of parameters is wrong`);
  |        }
  |        for (let i = 0; i < x.length; ++i) {
  |          closEnv.set(code.x.es[i].v.x, x[i]);
  |        }
  |        return runRec(code.e, closEnv);
  |      };
  |    }
  |    else if (code instanceof Var) {
  |      if (code.x === "true") {
  |        return true;
  |      }
  |      else if (code.x === "false") {
  |        return false;
  |      }
  |      else if (code.x === "error") {
  |        throw new Error(`an error is thrown`);
  |      }
  |      if (env.has(code.x)) {
  |        return env.get(code.x);
  |      }
  |      else {
  |        throw new Error(`unbound variable ${code}`);
  |      }
  |    }
  |    else if (code instanceof App) {
  |      if (isOp(code.f)) {
  |        const lhs = runRec(code.xs[0], env);
  |        const rhs = runRec(code.xs[1], env);
  |        switch (code.f.x) {
  |          case '+':
  |            return lhs + rhs;
  |          case '-':
  |            return lhs - rhs;
  |          case '*':
  |            return lhs * rhs;
  |          case '/':
  |            return lhs / rhs;
  |          case '==':
  |            return lhs == rhs;
  |          case '>':
  |            return lhs > rhs;
  |          case '<':
  |            return lhs < rhs;
  |          default:
  |            throw new Error(`unsupported operator ${code.f}`);
  |        }
  |      }
  |      else {
  |        const f = runRec(code.f, env);
  |        const params = code.xs.es.map(f => runRec(f.v, env));
  |        return f(params);
  |      }
  |    }
  |    else if (code instanceof Bra) {
  |      return runRec(code.x, env);
  |    }
  |    else if (code instanceof Let) {
  |      const nestEnv = new Map(env);
  |      nestEnv.set(code.nme.x, runRec(code.v, env));
  |      return runRec(code.bod, nestEnv);
  |    }
  |    else if (code instanceof CaseOf) {
  |      const term = runRec(code.trm, env);
  |      return runCase(term, code.cse, env);
  |    }
  |    else {
  |      throw new Error(`unsupported quasiquote ${code}`);
  |    }
  |  }
  |  globalThis.run = (code) => runRec(code, new Map());
  |})();
  """.stripMargin
}
