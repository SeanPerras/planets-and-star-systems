module.exports = {
    testMatch: ["<rootDir>/src/**/*.test.{js,jsx}"],
    transform: {
        "^.+\\.[jt]sx?$": "<rootDir>/jest-esbuild-transformer.cjs",
    },
    moduleNameMapper: {
        "\\.(css|less|scss|sass)$": "<rootDir>/test/styleMock.cjs",
    },
    collectCoverageFrom: [
        "src/**/*.{js,jsx}",
        "!src/**/*.test.{js,jsx}",
        "!src/main.jsx",
    ]
};