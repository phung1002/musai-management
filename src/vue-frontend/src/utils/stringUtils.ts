export const shortenFileName = (name: string): string => {
  if (!name) return "";
  const maxLength = 10;
  return name.length > maxLength
    ? name.slice(0, 5) + "..." + name.slice(-5)
    : name;
};
