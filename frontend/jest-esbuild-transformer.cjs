const esbuild = require("esbuild");

module.exports = {
    process(sourceText, sourcePath)
    {
        const result = esbuild.transformSync(sourceText, {
            loader: sourcePath.endsWith(".jsx") ? "jsx" : "js",
            format: "cjs",
            target: "node18",
            jsx: "automatic",
            sourcemap: "inline",
            sourcefile: sourcePath,
        });

        return {
            code: result.code,
        };
    },
};