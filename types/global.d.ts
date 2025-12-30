// Provide very small ambient declarations so the TS checker in this environment
// can resolve React/React Native/Expo modules and allow JSX usage while
// the full node_modules (and @types packages) are not installed.

declare namespace JSX {
  // allow any intrinsic elements
  interface IntrinsicElements {
    [elemName: string]: any;
  }
  interface IntrinsicAttributes {
    [key: string]: any;
  }
  interface IntrinsicClassAttributes<T> {
    [key: string]: any;
  }
  interface Element { }
  interface ElementClass { }
  interface ElementAttributesProperty { props: any }
  interface LibraryManagedAttributes<C, P> { [key: string]: any }
}

declare const React: any;
declare module 'react' {
  export = React;
}

declare const ReactNative: any;
declare module 'react-native' {
  const RN: any;
  export = RN;
}

declare module 'expo-router' {
  const ExpoRouter: any;
  export = ExpoRouter;
}

declare module 'expo-image' {
  const ExpoImage: any;
  export = ExpoImage;
}

declare module 'expo' {
  const Expo: any;
  export = Expo;
}

// Fallback for other common expo/react-native modules (optional)
declare module 'expo-constants' { const x: any; export = x }
declare module 'expo-status-bar' { const x: any; export = x }
