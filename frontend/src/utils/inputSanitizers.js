const limit = (value, maxLength) => String(value ?? "").slice(0, maxLength);

export const onlyDigits = (value, maxLength = 255) =>
    limit(value, maxLength).replace(/\D/g, "");

export const phoneDigits = (value, maxLength = 20) =>
    limit(value, maxLength).replace(/[^\d+()\s-]/g, "");

export const onlyLetters = (value, maxLength = 255) =>
    limit(value, maxLength).replace(/[^\p{L}\s'-]/gu, "");

export const safeText = (value, maxLength = 255) =>
    limit(value, maxLength).replace(/[<>]/g, "");

export const emailValue = (value, maxLength = 100) =>
    limit(value, maxLength)
        .replace(/\s/g, "")
        .replace(/[^A-Za-z0-9._%+@-]/g, "")
        .toLowerCase();

export const authIdentifier = (value, maxLength = 100) =>
    limit(value, maxLength)
        .replace(/\s/g, "")
        .replace(/[^A-Za-z0-9._@-]/g, "");

export const isValidEmail = (value) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(String(value ?? "").trim());
